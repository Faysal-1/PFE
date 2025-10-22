$(".loader-holder").fadeOut(300, function () {
    $("#main").animate({
        opacity: "1"
    }, 600);
});
//   Background image ------------------
var a = $(".bg");
a.each(function () {
    if ($(this).attr("data-bg")) $(this).css("background-image", "url(" + $(this).data("bg") + ")");
});
(function($){$.fn.downCount=function(options,callback){var settings=$.extend({date:null,offset:null},options);if(!settings.date){$.error('Date is not defined.')}if(!Date.parse(settings.date)){$.error('Incorrect date format, it should look like this, 12/24/2012 12:00:00.')}var container=this;var currentDate=function(){var date=new Date();var utc=date.getTime()+(date.getTimezoneOffset()*60000);var new_date=new Date(utc+(3600000*settings.offset));return new_date};function countdown(){var target_date=new Date(settings.date),current_date=currentDate();var difference=target_date-current_date;if(difference<0){clearInterval(interval);if(callback&&typeof callback==='function')callback();return}var _second=1000,_minute=_second*60,_hour=_minute*60,_day=_hour*24;var days=Math.floor(difference/_day),hours=Math.floor((difference%_day)/_hour),minutes=Math.floor((difference%_hour)/_minute),seconds=Math.floor((difference%_minute)/_second);days=(String(days).length>=2)?days:'0'+days;hours=(String(hours).length>=2)?hours:'0'+hours;minutes=(String(minutes).length>=2)?minutes:'0'+minutes;seconds=(String(seconds).length>=2)?seconds:'0'+seconds;var ref_days=(days===1)?'day':'days',ref_hours=(hours===1)?'hour':'hours',ref_minutes=(minutes===1)?'minute':'minutes',ref_seconds=(seconds===1)?'second':'seconds';container.find('.days').text(days);container.find('.hours').text(hours);container.find('.minutes').text(minutes);container.find('.seconds').text(seconds);container.find('.days_ref').text(ref_days);container.find('.hours_ref').text(ref_hours);container.find('.minutes_ref').text(ref_minutes);container.find('.seconds_ref').text(ref_seconds)};var interval=setInterval(countdown,1000)}})(jQuery);    
if ($(".counter-widget").length > 0) {
	var countCurrent = $(".counter-widget").attr("data-countDate");
	$(".countdown").downCount({
		date: countCurrent ,
		offset: 0
	});
}	
    if ($(".slideshow-container_wrap").length > 0) {
        var ms1 = new Swiper(".slideshow-container_wrap .swiper-container", {
            preloadImages: false,
            loop: true,
            speed: 1400,
            spaceBetween: 0,
            effect: "fade",
            init: false,
            autoplay: {
                delay: 2500,
                disableOnInteraction: false
            },
            pagination: {
                el: '.hero-slider-pagination',
                clickable: true,
            },
        });
        setTimeout(function () {
            ms1.init();
        }, 1000);
		
        ms1.on("slideChangeTransitionStart", function () {
            $(".slider-progress-bar").removeClass("act-slider");
        });
        ms1.on("slideChangeTransitionEnd", function () {
            $(".slider-progress-bar").addClass("act-slider");
        });		
		
    }
     //   mailchimp------------------
    $("#subscribe").ajaxChimp({
        language: "eng",
        url: "https://gmail.us1.list-manage.com/subscribe/post?u=1fe818378d5c129b210719d80&amp;id=a2792f681b"
    });
    $.ajaxChimp.translations.eng = {
        submit: "Submitting...",
        0: '<i class="fal fa-check"></i> We will be in touch soon!',
        1: '<i class="fal fa-exclamation-circle"></i> You must enter a valid e-mail address.',
        2: '<i class="fal fa-exclamation-circle"></i> E-mail address is not valid.',
        3: '<i class="fal fa-exclamation-circle"></i> E-mail address is not valid.',
        4: '<i class="fal fa-exclamation-circle"></i> E-mail address is not valid.',
        5: '<i class="fal fa-exclamation-circle"></i> E-mail address is not valid.'
    };	

function csselem() {
    $(".slideshow-container .slideshow-item").css({
        height: $(".slideshow-container").outerHeight(true)
    });
}
csselem();
var $window = $(window);
$window.on("resize", function () {
    csselem();
});
//   Contact form------------------
$(document).on('submit', '#contactform', function () {
    var a = $(this).attr("action");
    $("#message").slideUp(750, function () {
        $("#message").hide();
        $("#submit").attr("disabled", "disabled");
        $.post(a, {
            name: $("#name").val(),
            email: $("#email").val(),
            comments: $("#comments").val()
        }, function (a) {
            document.getElementById("message").innerHTML = a;
            $("#message").slideDown("slow");
            $("#submit").removeAttr("disabled");
            if (null != a.match("success")) $("#contactform").slideDown("slow");
        });
    });
    return false;
});
$(document).on('keyup', '#contactform input, #contactform textarea', function () {
    $("#message").slideUp(1500);
});
$('.cf_btn').on("click", function (e) {
    e.preventDefault();
    $('.contact-form-wrap').fadeIn(400);
    $("html, body").addClass("hid-body");
});
$('.contact-form-overlay , .close-contact-form').on("click", function () {
    $('.contact-form-wrap').hide();
    $("html, body").removeClass("hid-body");
});