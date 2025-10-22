 // All functions  ------------------
 function initTheroof() {
	 $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">');
     "use strict";
     $(".loader-holder").fadeOut(500, function() {
         $("#main").animate({
             opacity: "1"
         }, 500);
     });
     $(".style-select").niceSelect();
     //   lightGallery------------------
     function lightGalleryInit() {
         $(".image-popup").lightGallery({
             selector: "this",
             cssEasing: "cubic-bezier(0.25, 0, 0.25, 1)",
             download: false,
             counter: false
         });
         var o = $(".lightgallery"),
             p = o.data("looped");
         o.lightGallery({
             selector: ".lightgallery a.popup-image",
             cssEasing: "cubic-bezier(0.25, 0, 0.25, 1)",
             download: false,
             loop: false,
             counter: false
         });
         $('#html5-videos').lightGallery({
             selector: 'this',
             counter: false,
             download: false,
             zoom: false
         });
     }
     lightGalleryInit();
     // tabs ------------------
     $(".tabs-menu a").on("click", function(a) {
         a.preventDefault();
         $(this).parent().addClass("current");
         $(this).parent().siblings().removeClass("current");
         var b = $(this).attr("href");
         $(this).parents(".tabs-act").find(".tab-content").not(b).css("display", "none");
         $(b).fadeIn();
     });
     //   accordion ------------------
     $(".accordion-lite-header").on("click", function() {
         $(this).parent(".accordion-lite-container").find(".accordion-lite_content").slideToggle(400);
         $(this).toggleClass("acc_open");
     });
     //scroll animation------------------
     $(".piechart-holder").appear(function() {
         $(this).find(".chart").each(function() {
             var a = $(".piechart-holder").data("skcolor");
             var b = $(".piechart-holder").data("trcolor");
             $(".chart").easyPieChart({
                 barColor: a,
                 trackColor: b,
                 scaleColor: "#9ACFB7",
                 size: "120",
                 lineWidth: "30",
                 lineCap: "butt",
                 onStep: function(a, b, c) {
                     $(this.el).find(".percent").text(Math.round(c));
                 }
             });
         });
     });
     $(".skillbar-box").appear(function() {
         $(this).find("div.skillbar-bg").each(function() {
             $(this).find(".custom-skillbar").delay(600).animate({
                 width: $(this).attr("data-percent")
             }, 1500);
         });
     });
     $(".stats").appear(function() {
         $(".num").countTo();
     });
     // isotope------------------
     function n() {
         if ($(".gallery-items").length) {
             var $grid = $(".gallery-items").isotope({
                 singleMode: true,
                 columnWidth: ".grid-sizer, .grid-sizer-second, .grid-sizer-three",
                 itemSelector: ".gallery-item, .gallery-item-second, .gallery-item-three"
             });
             $grid.imagesLoaded(function() {
                 $grid.isotope("layout");
             });
             $(".gallery-filters").on("click", "a.gallery-filter", function(b) {
                 b.preventDefault();
                 var c = $(this).attr("data-filter"),
                     d = $(this).text();
                 if ($(".gallery-items").hasClass('sf_true')) {
                     setTimeout(function() {
                         $grid.isotope({
                             filter: c
                         });
                     }, 500);
                     $('html, body').animate({
                         scrollTop: $("#scroll-folio").offset().top - 80
                     }, 400);
                 } else {
                     $grid.isotope({
                         filter: c
                     });
                 }
                 $(".gallery-filters a").removeClass("gallery-filter-active");
                 $(this).addClass("gallery-filter-active");
             });
             var gat = $(".gallery-filter-active").text();
         }
         $(".gallery-items").isotope("on", "layoutComplete", function(a, b) {
             var b = a.length;
             $(".num-album").html(b);
         });
         var b = $(".gallery-item").length;
         $(".all-album , .num-album").html(b);
     }
     n();
     $(".show-hidden-filters").on("click", function() {
         $(this).toggleClass("shf_btn_active");
         $(".hidden-filter").toggleClass("vis_hiddenfilter");
     });
     // Other functions ------------------
     var n = $(".bg");
     n.each(function(a) {
         if ($(this).attr("data-bg")) $(this).css("background-image", "url(" + $(this).data("bg") + ")");
     });
     $(".fix-bar").scrollToFixed({
         minWidth: 1064,
         marginTop: 90,
         removeOffsets: true,
         limit: function() {
             var a = $(".limit-box").offset().top - $(".fix-bar").outerHeight(true) + 36;
             return a;
         }
     });
     if ($(".fixed-bar").outerHeight(true) < $(".post-container").outerHeight(true)) {
         $(".fixed-bar").addClass("fixbar-action");
         $(".fixbar-action").scrollToFixed({
             minWidth: 1064,
             marginTop: function() {
                 var a = $(window).height() - $(".fixed-bar").outerHeight(true) - 120;
                 if (a >= 0) return 20;
                 return a;
             },
             removeOffsets: true,
             limit: function() {
                 var a = $(".limit-box").offset().top - $(".fixed-bar").outerHeight();
                 return a;
             }
         });
     } else $(".fixed-bar").removeClass("fixbar-action");

     function o() {
         $("form[name=cart]").jAutoCalc("destroy");
         $("form[name=cart]").jAutoCalc({
             keyEventsFire: true,
             decimalPlaces: 2,
             emptyAsZero: true
         });
     }
     o();
     $(".rage-slider").ionRangeSlider();
     $(".shop-price").ionRangeSlider({
         keyboard: true,
         prefix: "$"
     });
     var maxL = 90;
     $(".dbig p").each(function(i, div) {
         var text = $(div).text();
         if (text.length > maxL) {
             var begin = text.substr(0, maxL),
                 end = text.substr(maxL);
             $(div).html(begin).append($('<div class="hidden" />').html(end));
         }
     });
     $(".custom-scroll-link").on("click", function() {
         var a = 50;
         if (location.pathname.replace(/^\//, "") === this.pathname.replace(/^\//, "") || location.hostname === this.hostname) {
             var b = $(this.hash);
             b = b.length ? b : $("[name=" + this.hash.slice(1) + "]");
             if (b.length) {
                 $("html,body").animate({
                     scrollTop: b.offset().top - a
                 }, {
                     queue: false,
                     duration: 1200,
                     easing: "easeInOutExpo"
                 });
                 return false;
             }
         }
     });
     //   Contact form------------------
     $(document).on('submit', '#contactform', function() {
         var a = $(this).attr("action");
         $("#message").slideUp(750, function() {
             $("#message").hide();
             $("#submit").attr("disabled", "disabled");
             $.post(a, {
                 name: $("#name").val(),
                 email: $("#email").val(),
                 comments: $("#comments").val()
             }, function(a) {
                 document.getElementById("message").innerHTML = a;
                 $("#message").slideDown("slow");
                 $("#submit").removeAttr("disabled");
                 if (null != a.match("success")) $("#contactform").slideDown("slow");
             });
         });
         return false;
     });
     $(document).on('keyup', '#contactform input, #contactform textarea', function() {
         $("#message").slideUp(1500);
     });
     $('.cf_btn').on("click", function(e) {
         e.preventDefault();
         $('.contact-form-wrap').fadeIn(400);
         $("html, body").addClass("hid-body");
         $("#message").slideUp(100);
         $(".custom-form").find("input[type=text], textarea").val("");
     });
     $('.contact-form-overlay , .close-contact-form').on("click", function() {
         $('.contact-form-wrap').hide();
         $("html, body").removeClass("hid-body");
     });
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
     //   Video------------------	
     function videoint() {
         var v = $(".background-youtube-wrapper").data("vid");
         var f = $(".background-youtube-wrapper").data("mv");
         $(".background-youtube-wrapper").YTPlayer({
             fitToBackground: true,
             videoId: v,
             pauseOnScroll: true,
             mute: f,
             callback: function() {
                 var a = $(".background-youtube-wrapper").data("ytPlayer").player;
             }
         });
         var w = $(".background-vimeo").data("vim"),
             bvc = $(".background-vimeo"),
             bvmc = $(".media-container"),
             bvfc = $(".background-vimeo iframe "),
             vch = $(".video-container");
         bvc.append('<iframe src="//player.vimeo.com/video/' + w + '?background=1"  frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen ></iframe>');
         $(".video-holder").height(bvmc.height());
         if ($(window).width() > 1024) {
             if ($(".video-holder").length > 0)
                 if (bvmc.height() / 9 * 16 > bvmc.width()) {
                     bvfc.height(bvmc.height()).width(bvmc.height() / 9 * 16);
                     bvfc.css({
                         "margin-left": -1 * $("iframe").width() / 2 + "px",
                         top: "-75px",
                         "margin-top": "0px"
                     });
                 }
             else {
                 bvfc.width($(window).width()).height($(window).width() / 16 * 9);
                 bvfc.css({
                     "margin-left": -1 * $("iframe").width() / 2 + "px",
                     "margin-top": -1 * $("iframe").height() / 2 + "px",
                     top: "50%"
                 });
             }
         } else if ($(window).width() < 760) {
             $(".video-holder").height(bvmc.height());
             bvfc.height(bvmc.height());
         } else {
             $(".video-holder").height(bvmc.height());
             bvfc.height(bvmc.height());
         }
         vch.css("width", $(window).width() + "px");
         vch.css("height", Number(720 / 1280 * $(window).width()) + "px");
         if (vch.height() < $(window).height()) {
             vch.css("height", $(window).height() + "px");
             vch.css("width", Number(1280 / 720 * $(window).height()) + "px");
         }
     }
     videoint();
     $(".to-top").on("click", function(a) {
         a.preventDefault();
         $("html, body").animate({
             scrollTop: 0
         }, 800);
         return false;
     });
     if ($(".header-cart_wrap_container").length > 0) {
         var aps = new PerfectScrollbar('.header-cart_wrap_container', {
             swipeEasing: true,
             minScrollbarLength: 20
         });
     }
     var scwrp = $(".header-search-wrap"),
         swlink = $(".show_search-btn");
     function showSearch() {
         scwrp.fadeIn(1).addClass("vis-search").removeClass("novis_sarch")
         swlink.addClass("scwllink2");
         hideCart();
         hideShare();
     }
     function hideSearch() {
         scwrp.fadeOut(1).removeClass("vis-search").addClass("novis_sarch");
         swlink.removeClass("scwllink2");
     }
     swlink.on("click", function() {
         if (scwrp.hasClass("novis_sarch")) showSearch();
         else hideSearch();
     });
     var wlwrp = $(".header-cart_wrap"),
         wllink = $(".sc_btn");
     function showCart() {
         wlwrp.fadeIn(1).addClass("vis-cart").removeClass("novis_cart")
         aps.update();
         wllink.addClass("scwllink");
         hideShare();
         hideSearch();
     }
     function hideCart() {
         wlwrp.fadeOut(1).removeClass("vis-cart").addClass("novis_cart");
         wllink.removeClass("scwllink");
     }
     wllink.on("click", function() {
         if (wlwrp.hasClass("novis_cart")) showCart();
         else hideCart();
     });
     // Share   ------------------
     $(".share-container").share({
         networks: ['facebook', 'pinterest', 'twitter', 'linkedin', 'tumblr']
     });
     var swra = $(".share-wrapper"),
         clsh = $(".close-share-btn"),
         ssbtn = $(".showshare");
     function showShare() {
         ssbtn.addClass("uncl-share");
         swra.removeClass("isShare").addClass("share-wrapper_vis");
         hideSearch();
         hideCart();
     }
     function hideShare() {
         ssbtn.removeClass("uncl-share");
         swra.addClass("isShare").removeClass("share-wrapper_vis");
     }
     clsh.on("click", function() {
         hideShare();
     });
     ssbtn.on("click", function() {
         if (swra.hasClass("isShare")) showShare();
         else hideShare();
     });
     $(".show-reg-form").on("click", function() {
         showModal();
     });
     function showModal() {
         $(".main-register-container").fadeIn(1);
         $(".main-register").addClass("vis_mr");
     }
     function hideModal() {
         $(".main-register-container").fadeOut(1);
         $(".main-register").removeClass("vis_mr");
     }
     $(".close-reg-form").on("click", function() {
         hideModal();
     });
     // duplicate -----------------
     $.fn.duplicate = function(a, b) {
         var c = [];
         for (var d = 0; d < a; d++) $.merge(c, this.clone(b).get());
         return this.pushStack(c);
     };
     var cr = $(".star-rating");
     cr.each(function(cr) {
         var starcount = $(this).attr("data-starrating");
         $("<i class='fas fa-star'></i>").duplicate(starcount).prependTo(this);
     });
     $(window).on("scroll", function() {
         var a = $(document).height();
         var b = $(window).height();
         var c = $(window).scrollTop();
         var d = c / (a - b) * 100;
         $(".progress-bar").css({
             width: d + "%"
         });
         if ($(this).scrollTop() > 150) {
             $(".top-bar").addClass("scroll-sticky");
         } else {
             $(".top-bar").removeClass("scroll-sticky");
         }
     });
     $("<span class='footer-bg-pin'></span>").duplicate(4).prependTo(".footer-bg");
     function heroAnim() {
         function a(a) {
             var b = a.length,
                 c, d;
             while (b) {
                 d = Math.floor(Math.random() * b--);
                 c = a[b];
                 a[b] = a[d];
                 a[d] = c;
             }
             return a;
         }
         var b = $(".footer-bg-pin");
         $(a(b).slice(0, $(".footer-bg").data("ran"))).each(function(a) {
             var bc = $(this);
             b.removeClass("footer-bg-pin-vis")
             bc.addClass("footer-bg-pin-vis");
         });
     }
     setInterval(function() {
         heroAnim();
     }, 2000);
     if ($(".hero-slider").length > 0) {
         var hs = new Swiper(".hero-slider .swiper-container", {
             preloadImages: false,
             loop: true,
             grabCursor: true,
             speed: 2400,
             spaceBetween: 0,
             effect: "slide",
             mousewheel: false,
             slidesPerView: '1',
             parallax: true,
             pagination: {
                 el: '.hero-slider-pagination',
                 clickable: true,
             },
             navigation: {
                 nextEl: '.hero-slider-cont-next',
                 prevEl: '.hero-slider-cont-prev',
             },
             autoplay: {
                 delay: 4000,
                 disableOnInteraction: false
             }
         });
         hs.on("slideChangeTransitionStart", function() {
             $(".slider-progress-bar").removeClass("act-slider");
         });
         hs.on("slideChangeTransitionEnd", function() {
             $(".slider-progress-bar").addClass("act-slider");
         });
         var autobtn = $(".play-pause_slider");

         function autoEnd() {
             autobtn.removeClass("auto_actslider");
             hs.autoplay.stop();
         }
         function autoStart() {
             autobtn.addClass("auto_actslider");
             hs.autoplay.start();
         }
         autobtn.on("click", function() {
             if (autobtn.hasClass("auto_actslider")) autoEnd();
             else autoStart();
             return false;
         });
     }
     if ($(".fw-carousel").length > 0) {
         var totalSlides2 = $(".fw-carousel .swiper-slide").length;
         var mouseContr = $(".fw-carousel").data("mousecontrol");
         var j2 = new Swiper(".fw-carousel .swiper-container", {
             preloadImages: false,
             loop: true,
             freeMode: false,
             slidesPerView: 'auto',
             spaceBetween: 10,
             grabCursor: true,
             mousewheel: mouseContr,
             speed: 1400,
             centeredSlides: true,
             effect: "slide",
             pagination: {
                 el: '.ss-slider-pagination',
                 clickable: true,
             },
             navigation: {
                 nextEl: '.fw-carousel-button-next',
                 prevEl: '.fw-carousel-button-prev',
             },
         });
     }
     if ($(".single-slider").length > 0) {
         var j2 = new Swiper(".single-slider .swiper-container", {
             preloadImages: false,
             slidesPerView: 1,
             spaceBetween: 0,
             loop: true,
             autoHeight: false,
             grabCursor: true,
             mousewheel: false,
             pagination: {
                 el: '.ss-slider-pagination',
                 clickable: true,
             },
             navigation: {
                 nextEl: '.ss-slider-cont-next',
                 prevEl: '.ss-slider-cont-prev',
             },
         });
     }
     if ($(".clients-carousel").length > 0) {
         var j2 = new Swiper(".clients-carousel .swiper-container", {
             preloadImages: false,
             freeMode: false,
             slidesPerView: 5,
             spaceBetween: 10,
             loop: true,
             grabCursor: true,
             mousewheel: false,
             navigation: {
                 nextEl: '.cc-next',
                 prevEl: '.cc-prev',
             },
             breakpoints: {
                 1064: {
                     slidesPerView: 3,
                 },
                 768: {
                     slidesPerView: 2,
                 },
                 520: {
                     slidesPerView: 1,
                 },
             }
         });
     }
     if ($(".testimonilas-carousel").length > 0) {
         var j2 = new Swiper(".testimonilas-carousel .swiper-container", {
             preloadImages: false,
             slidesPerView: 4,
             spaceBetween: 10,
             loop: true,
             grabCursor: true,
             mousewheel: false,
             centeredSlides: true,
             pagination: {
                 el: '.tc-pagination',
                 clickable: true,
             },
             navigation: {
                 nextEl: '.tc-button-next',
                 prevEl: '.tc-button-prev',
             },
             breakpoints: {
                 1264: {
                     slidesPerView: 2,
                 },
                 630: {
                     slidesPerView: 1,
                 },
             }
         });
     }
     if ($(".team-slider ").length > 0) {
         var teamCarousel = new Swiper(".team-slider .swiper-container", {
             preloadImages: true,
             loop: true,
             centeredSlides: false,
             freeMode: false,
             slidesPerView: 3,
             spaceBetween: 20,
             grabCursor: true,
             mousewheel: false,
             parallax: false,
             speed: 1400,
             navigation: {
                 nextEl: '.ts-cont-next',
                 prevEl: '.ts-cont-prev',
             },
             breakpoints: {
                 1064: {
                     slidesPerView: 2,
                     centeredSlides: true,
                 },
                 640: {
                     slidesPerView: 1,
                     centeredSlides: false,
                 },
             }
         });
     }
     if ($(".hero-counter-slider").length > 0) {
         var teamCarousel = new Swiper(".hero-counter-slider .swiper-container", {
             preloadImages: true,
             loop: true,
             centeredSlides: false,
             freeMode: false,
             slidesPerView: 3,
             spaceBetween: 0,
             grabCursor: true,
             mousewheel: false,
             parallax: false,
             speed: 1400,
             autoplay: {
                 delay: 3000,
                 disableOnInteraction: false
             },
             navigation: {
                 nextEl: '.hcc-next',
                 prevEl: '.hcc-prev',
             },
             breakpoints: {
                 768: {
                     slidesPerView: 2,
                 },
                 640: {
                     slidesPerView: 1,
                 },
             }
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
         setTimeout(function() {
             ms1.init();
         }, 1000);
         ms1.on("slideChangeTransitionStart", function() {
             $(".slider-progress-bar").removeClass("act-slider");
         });
         ms1.on("slideChangeTransitionEnd", function() {
             $(".slider-progress-bar").addClass("act-slider");
         });
     }
     if ($(".half-carousel-wrap").length > 0) {
         var teamCarousel = new Swiper(".half-carousel .swiper-container", {
             preloadImages: true,
             loop: true,
             centeredSlides: true,
             freeMode: false,
             slidesPerView: 3,
             spaceBetween: 0,
             grabCursor: true,
             mousewheel: false,
             parallax: true,
             speed: 1400,
             navigation: {
                 nextEl: '.hcw-cont-next',
                 prevEl: '.hcw-cont-prev',
             },
             breakpoints: {
                 768: {
                     slidesPerView: 2,
                 },
                 640: {
                     slidesPerView: 1,
                 },
             }
         });
     }
     $(".scroll-init").singlePageNav({
         filter: ":not(.external)",
         updateHash: false,
         offset: 80,
         threshold: 120,
         speed: 1200,
         currentClass: "act-scrlink"
     });
     var dotsCanvas = {
         init: function() {
             var $blocks = $('[data-dots]');
             $blocks.each(function() {
                 var $block = $(this);
                 var block = $block[0];
                 var $canvas = $("<canvas/>").appendTo($block);
                 var canvas = $canvas[0];
                 var width = $block.width();
                 var height = $block.height();
                 var ctx = canvas.getContext('2d');
                 ctx.width = width;
                 ctx.height = height;
                 var devicePixelRatio = window.devicePixelRatio || 1,
                     backingStoreRatio = ctx.webkitBackingStorePixelRatio || ctx.mozBackingStorePixelRatio || ctx.msBackingStorePixelRatio || ctx.oBackingStorePixelRatio || ctx.backingStorePixelRatio || 1;
                 var ratio = devicePixelRatio / backingStoreRatio;
                 canvas.width = width * ratio;
                 canvas.height = height * ratio;
                 ctx.scale(ratio, ratio);
                 var mouseX = undefined;
                 var mouseY = undefined;

                 function Circle(x, y) {
                     this.x = x;
                     this.y = y;
                     this.distance = 10;
                     this.radians = 0;
                     this.draw = function() {
                         ctx.beginPath();
                         ctx.strokeStyle = 'rgba(151, 151, 151, .5)';
                         ctx.moveTo(this.x + 3, this.y);
                         ctx.lineTo(this.x + 3, this.y + 6);
                         ctx.moveTo(this.x, this.y + 3);
                         ctx.lineTo(this.x + 6, this.y + 3);
                         ctx.stroke();
                     };
                     this.update = function() {
                         if (mouseX > -1) {
                             var k1 = mouseY - y;
                             var k2 = mouseX - x;
                             var tan = k1 / k2;
                             var yrad = Math.atan(tan);
                             if (mouseX < x) {
                                 yrad = Math.PI - Math.atan(-tan);
                             }
                             this.x = x + Math.cos(yrad) * this.distance;
                             this.y = y + Math.sin(yrad) * this.distance;
                         }
                         this.draw();
                     };
                 };
                 var circlesArray = [];
                 var gutter = 40;
                 var countW = Math.floor(width / gutter);
                 var countH = Math.floor(height / gutter);
                 var len = countW * countH;
                 for (var i = 0; i < countH; i++) {
                     for (var t = 0; t < countW; t++) {
                         var x = gutter * t;
                         var y = gutter * i;
                         circlesArray.push(new Circle(x, y));
                     }
                 }
                 var loop = function() {
                     ctx.clearRect(0, 0, ctx.width, ctx.height);
                     for (var i = 0; i < circlesArray.length; i++) {
                         circlesArray[i].update();
                     }
                 };
                 document.addEventListener('mousemove', function(e) {
                     var parentOffset = $(canvas).parent().offset();
                     var relX = e.pageX - parentOffset.left;
                     var relY = e.pageY - parentOffset.top;
                     mouseX = relX;
                     mouseY = relY;
                     ctx.clearRect(0, 0, ctx.width, ctx.height);
                     for (var i = 0; i < circlesArray.length; i++) {
                         circlesArray[i].update();
                     }
                     loop();
                 });
                 loop();
             });
         }
     };
     window.requestAnimationFrame = (function() {
         return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || function(callback) {
             window.setTimeout(callback, 1000 / 60);
         };
     })();
     window.cancelAnimationFrame = (function() {
         return window.cancelAnimationFrame || window.webkitCancelAnimationFrame || window.mozCancelAnimationFrame || function(timPtr) {
             window.clearTimeout(timPtr);
         };
     })();
     dotsCanvas.init();

     function csselem() {
         $(".slideshow-container .slideshow-item").css({
             height: $(".slideshow-container").outerHeight(true)
         });
         $(".height-emulator").css({
             height: $(".main-footer").outerHeight(true)
         });
         $(".fs-slider-item").css({
             height: $(".hero-wrap").outerHeight(true)
         });
     }
     csselem();
     // Mob Menu------------------
     $(".nav-button-wrap").on("click", function() {
         $(".main-menu").toggleClass("vismobmenu");
     });

     function mobMenuInit() {
         var ww = $(window).width();
         if (ww < 1048) {
             $(".menusb").remove();
             $(".main-menu").removeClass("nav-holder");
             $(".main-menu nav").clone().addClass("menusb").appendTo(".main-menu");
             $(".menusb").menu();
         } else {
             $(".menusb").remove();
             $(".main-menu").addClass("nav-holder");
         }
     }
     mobMenuInit();
     //   css ------------------
     var $window = $(window);
     $window.on("resize", function() {
         csselem();
         mobMenuInit();
     });
     $(".show-hidd-filters").on("click", function(w) {
         w.preventDefault();
         $(".mob-hid_filters").slideToggle(300);
     });
 }
 // parallax------------------
 function initparallax() {
     var a = {
         Android: function() {
             return navigator.userAgent.match(/Android/i);
         },
         BlackBerry: function() {
             return navigator.userAgent.match(/BlackBerry/i);
         },
         iOS: function() {
             return navigator.userAgent.match(/iPhone|iPad|iPod/i);
         },
         Opera: function() {
             return navigator.userAgent.match(/Opera Mini/i);
         },
         Windows: function() {
             return navigator.userAgent.match(/IEMobile/i);
         },
         any: function() {
             return a.Android() || a.BlackBerry() || a.iOS() || a.Opera() || a.Windows();
         }
     };
     trueMobile = a.any();
     if (null === trueMobile) {
         var b = new Scrollax();
         b.reload();
         b.init();
     }
     if (trueMobile) $(".background-vimeo , .background-youtube-wrapper ").remove();
 }
 document.addEventListener('gesturestart', function (e) {
    e.preventDefault();
}); 
 // Init all functions------------------
 initTheroof();
 initparallax();
 