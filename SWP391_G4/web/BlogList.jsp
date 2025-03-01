<%-- 
    Document   : BlogList
    Created on : Feb 3, 2025, 2:53:16 PM
    Author     : admin
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Blog Page</title>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" rel="stylesheet">
        <link href="./css/blog.css" rel="stylesheet"> <!-- Link tới CSS tùy chỉnh -->
    </head>
    <body>
        <jsp:include page="/navbar.jsp"></jsp:include>
        <div id="main-content" class="blog-page">
            <div class="container">
                <div class="row clearfix">
                    <!-- Slider Start -->
                    <div class="container-fluid mb-4">
                        <c:if test="${not empty listSlider_HomePageAll}">
                            <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel" style="z-index: 1; position: relative;">
                                <ol class="carousel-indicators">
                                    <c:forEach var="slider" items="${listSlider_HomePageAll}" varStatus="status">
                                        <li data-target="#carouselExampleIndicators" data-slide-to="${status.index}" class="${status.index == 0 ? 'active' : ''}"></li>
                                        </c:forEach>
                                </ol>
                                <div class="carousel-inner">
                                    <c:forEach var="slider" items="${listSlider_HomePageAll}" varStatus="status">
                                        <div class="carousel-item ${status.index == 0 ? 'active' : ''}">
                                            <a href="${slider.backlink}" class="slider-link">
                                                <img src="SLIDER_IMAGE/${slider.sliderImage}" class="d-block w-100" alt="Slider Image">
                                                <div class="carousel-caption d-none d-md-block">
                                                    <h5>${slider.sliderTitle}</h5>
                                                    <h5>${slider.note}</h5>
                                                </div>
                                            </a>
                                        </div>
                                    </c:forEach>
                                </div>

                                <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                    <span class="sr-only">Previous</span>
                                </a>
                                <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                    <span class="sr-only">Next</span>
                                </a>
                            </div>
                        </c:if>
                    </div>
                    <!-- Slider End -->
                    <div style="clear: both"></div>

                    <!-- Main Blog Posts Section -->
                    <div class="col-lg-8 col-md-12 left-box">
                        <form action="blog-list" method="GET" class="mb-4">
                            <div class="input-group m-b-0">
                                <div class="input-group-prepend">
                                    <span class="input-group-text"><i class="fa fa-search"></i></span>
                                </div>
                                <input type="text" name="blogName" class="form-control" value="${blogName}" placeholder="Search...">
                                <button type="submit" class="btn btn-primary">Search</button>
                            </div>
                        </form>
                        <!-- Check if there are any blog posts -->
                        <c:if test="${empty blogList}">
                            <div class="alert alert-warning" role="alert">
                                No blogs found for your search criteria.
                            </div>
                        </c:if>
                        <c:forEach var="blog" items="${blogList}">
                            <div class="card single_post">
                                <div class="body">
                                    <div class="img-post">
                                        <img class="d-block img-fluid" src="${blog.thumbnail}" alt="${blog.title}">
                                    </div>
                                    <h3><a href="blog-details?blogId=${blog.blog_id}">${blog.title}</a></h3>
                                    <p>${blog.brief_infor}</p>
                                </div>
                                <div class="footer">
                                    <div class="actions">
                                        <a style="text-decoration: none" href="blog-details?blogId=${blog.blog_id}" class="btn-outline-custom">Continued Reading</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <!-- Pagination -->
                        <ul class="pagination pagination-primary">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?index=${currentPage - 1}&pageSize=${pageSize}&blogName=${blogName}&categoryId=${categoryId}">Previous</a>
                            </li>

                            <c:forEach var="i" begin="1" end="${num}">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="?index=${i}&pageSize=${pageSize}&blogName=${blogName}&categoryId=${categoryId}">${i}</a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage == num ? 'disabled' : ''}">
                                <a class="page-link" href="?index=${currentPage + 1}&pageSize=${pageSize}&blogName=${blogName}&categoryId=${categoryId}">Next</a>
                            </li>
                        </ul>
                    </div>

                    <!-- Sidebar Section (Right Box) -->
                    <div class="col-lg-4 col-md-12 right-box">
                        <!-- Categories Clouds -->
                        <div class="card">
                            <div class="header">
                                <h2>Categories Blog</h2>
                            </div>
                            <div class="body widget">
                                <ul class="list-unstyled categories-clouds m-b-0">
                                    <c:forEach var="category" items="${categories}">
                                        <li><a href="?categoryId=${category.categoryBlogId}&index=1">${category.categoryBlogName}</a></li>
                                        </c:forEach>
                                </ul>
                            </div>
                        </div>

                      
                    </div>
                </div>
            </div>
        </div>



        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>


