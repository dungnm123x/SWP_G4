<%-- 
    Document   : contact
    Created on : Mar 7, 2025, 10:12:37 AM
    Author     : dung9
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Contact</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">    
        <!-- Google reCAPTCHA -->
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <script src="https://smtpjs.com/v3/smtp.js"></script>

        <style>

            /* Conatct start */

            .header-title
            {
                text-align: center;
                color:#00bfff;
            }

            #tip
            {
                display:none;
            }

            .fadeIn
            {
                animation-duration: 3s;
            }

            .form-control
            {
                border-radius:0px;
                border:1px solid #EDEDED;
            }

            .form-control:focus
            {
                border:1px solid #00bfff;
            }

            .textarea-contact
            {
                resize:none;
            }

            .btn-send {
                background-color: #00bfff !important; /* Giữ màu xanh */
                color: white !important; /* Chữ trắng */
                border: 1px solid #00bfff !important;
            }

            .btn-send:hover {
                background-color: white !important;
                color: #00bfff !important;
                border: 1px solid #00bfff !important;
            }

            .second-portion
            {
                margin-top:50px;
            }


            .box > .icon {
                text-align: center;
                position: relative;
            }
            .box > .icon > .image {
                position: relative;
                z-index: 2;
                margin: auto;
                width: 88px;
                height: 88px;
                border: 8px solid white;
                line-height: 88px;
                border-radius: 50%;
                background: #00bfff;
                vertical-align: middle;
            }
            .box > .icon:hover > .image {
                background: #333;
            }
            .box > .icon > .image > i {
                font-size: 36px !important;
                color: #fff !important;
            }
            .box > .icon:hover > .image > i {
                color: white !important;
            }
            .box > .icon > .info {
                margin-top: -24px;
                background: rgba(0, 0, 0, 0.04);
                border: 1px solid #e0e0e0;
                padding: 15px 0 10px 0;
                min-height:163px;
            }
            .box > .icon:hover > .info {
                background: rgba(0, 0, 0, 0.04);
                border-color: #e0e0e0;
                color: white;
            }
            .box > .icon > .info > h3.title {
                font-family: "Robot",sans-serif !important;
                font-size: 16px;
                color: #222;
                font-weight: 700;
            }
            .box > .icon > .info > p {
                font-family: "Robot",sans-serif !important;
                font-size: 13px;
                color: #666;
                line-height: 1.5em;
                margin: 20px;
            }
            .box > .icon:hover > .info > h3.title, .box > .icon:hover > .info > p, .box > .icon:hover > .info > .more > a {
                color: #222;
            }
            .box > .icon > .info > .more a {
                font-family: "Robot",sans-serif !important;
                font-size: 12px;
                color: #222;
                line-height: 12px;
                text-transform: uppercase;
                text-decoration: none;
            }
            .box > .icon:hover > .info > .more > a {
                color: #fff;
                padding: 6px 8px;
                background-color: #63B76C;
            }
            .box .space {
                height: 30px;
            }

            @media only screen and (max-width: 768px)
            {
                .contact-form
                {
                    margin-top:25px;
                }

                .btn-send
                {
                    width: 100%;
                    padding:10px;
                }

                .second-portion
                {
                    margin-top:25px;
                }
            }
            /* Conatct end */
        </style>
    </head>
    <jsp:include page="/navbar.jsp"></jsp:include>
        <body>

            <div class="container animated fadeIn" style="margin-top: 80px;">
                <div class="row">
                    <hr>
                    <div class="col-sm-12" id="parent">
                        <div class="col-sm-6">
                            <iframe 
                                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.634374438614!2d105.5235173!3d21.0153229!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135abc60e7d3f19%3A0x2be9d7d0b5abcbf4!2sFPT+University!5e0!3m2!1svi!2s!4v1716273471328" 
                                width="100%" 
                                height="400" 
                                style="border:0;" 
                                allowfullscreen="" 
                                loading="lazy" 
                                referrerpolicy="no-referrer-when-downgrade">
                            </iframe>
                        </div>

                        <div class="col-sm-6">
                            <form action="contact" class="contact-form" method="post">
                                <div class="form-group">
                                    <input type="text" required placeholder="Name" name="name" class="form-control">
                                </div>
                                <div class="form-group form_left">
                                    <input type="email" required placeholder="Email" name="email" class="form-control">
                                </div>
                                <div class="form-group">
                                    <input type="text" required placeholder="Subject" name="subject" class="form-control">
                                </div>
                                <div class="form-group">
                                    <textarea class="form-control textarea-contact" rows="5" id="message" name="message" placeholder="Type Your Message/Feedback here..." required></textarea>
                                    <br>
                                    <div class="g-recaptcha" data-sitekey="6Lce-_wpAAAAALe_YJT0ytdKKTp_8f5eAoUjCif1"></div> 
                                    <br><br>                               
                                    <button type="submit" class="btn btn-default btn-send">
                                        <span class="glyphicon glyphicon-send"></span> Send
                                    </button>
                                </div>

                            <% String message = (String) request.getAttribute("message"); %>
                            <% String status = (String) request.getAttribute("status"); %>

                            <% if (message != null) { %>
                            <div style="color: <%="success".equals(status) ? "green" : "red"%>;">
                                <%= message %>
                            </div>
                            <% } %>

                        </form>
                    </div>
                </div>
            </div>

            <div class="container second-portion">
                <div class="row">
                    <!-- Boxes de Acoes -->
                    <div class="col-xs-12 col-sm-6 col-lg-4">
                        <div class="box">							
                            <div class="icon">
                                <div class="image"><i class="fa fa-envelope" aria-hidden="true"></i></div>
                                <div class="info">
                                    <h3 class="title">MAIL & WEBSITE</h3>
                                    <p>
                                        <i class="fa fa-envelope" aria-hidden="true"></i> dungnmhe173094@fpt.edu.vn
                                        <br>
                                        <br>
                                        <i class="fa fa-globe" aria-hidden="true"></i> http://localhost:9999/SWP391_G4/home
                                    </p>

                                </div>
                            </div>
                            <div class="space"></div>
                        </div> 
                    </div>

                    <div class="col-xs-12 col-sm-6 col-lg-4">
                        <div class="box">							
                            <div class="icon">
                                <div class="image"><i class="fa fa-mobile" aria-hidden="true"></i></div>
                                <div class="info">
                                    <h3 class="title">CONTACT</h3>
                                    <p>
                                        <i class="fa fa-mobile" aria-hidden="true"></i> (+84)-0123456789
                                        <br>
                                        <br>
                                        <i class="fa fa-mobile" aria-hidden="true"></i>  (+84)-0123456789
                                    </p>
                                </div>
                            </div>
                            <div class="space"></div>
                        </div> 
                    </div>

                    <div class="col-xs-12 col-sm-6 col-lg-4">
                        <div class="box">							
                            <div class="icon">
                                <div class="image"><i class="fa fa-map-marker" aria-hidden="true"></i></div>
                                <div class="info">
                                    <h3 class="title">ADDRESS</h3>
                                    <p>
                                        <i class="fa fa-map-marker" aria-hidden="true"></i> Group 4-Đại học FPT
                                    </p>
                                </div>
                            </div>
                            <div class="space"></div>
                        </div> 
                    </div>		    
                    <!-- /Boxes de Acoes -->

                    <!--My Portfolio  dont Copy this -->

                </div>
            </div>

        </div>
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <script>
            $(function () {
                $('form').submit(function (event) {
                    var recaptcha = $("#g-recaptcha-response").val();
                    if (recaptcha === "") {
                        event.preventDefault();
                        alert("Please complete the reCAPTCHA");
                    }
                });
            });

        </script>
    </body>
</html>

