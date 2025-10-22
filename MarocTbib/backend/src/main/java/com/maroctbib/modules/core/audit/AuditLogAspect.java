package com.maroctbib.modules.core.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    
    public AuditLogAspect(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(auditable)")
    public Object logAuditEvent(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        String principal = authentication != null ? authentication.getName() : "anonymous";
        String action = auditable.action();
        String resourceId = evaluateExpression(joinPoint, auditable.resourceId());
        String resourceType = auditable.resourceType();
        
        Map<String, Object> details = new HashMap<>();
        details.put("method", joinPoint.getSignature().toShortString());
        
        if (auditable.logParameters()) {
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            Object[] args = joinPoint.getArgs();
            
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null && !(args[i] instanceof HttpServletRequest)) {
                    details.put(paramNames != null && i < paramNames.length ? 
                        paramNames[i] : "arg" + i, args[i]);
                }
            }
        }
        
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(AuditLog.ActionType.valueOf(action));
        auditLog.setPrincipal(principal);
        auditLog.setResourceId(resourceId);
        auditLog.setResourceType(resourceType);
        auditLog.setDetails(details);
        auditLog.setClientIp(request.getRemoteAddr());
        auditLog.setUserAgent(request.getHeader("User-Agent"));
        
        try {
            Object result = joinPoint.proceed();
            auditLog.setDetails(objectMapper.convertValue(details, Map.class));
            return result;
        } catch (Exception e) {
            auditLog.setDetails(details);
            auditLog.setDetails(Map.of("error", e.getMessage()));
            throw e;
        } finally {
            try {
                auditLogRepository.save(auditLog);
            } catch (Exception e) {
                log.error("Failed to save audit log", e);
            }
        }
    }
    
    private String evaluateExpression(ProceedingJoinPoint joinPoint, String expression) {
        if (expression.isEmpty()) {
            return null;
        }
        
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("args", joinPoint.getArgs());
        
        try {
            Object value = expressionParser.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.warn("Failed to evaluate expression: {}", expression, e);
            return null;
        }
    }
}
