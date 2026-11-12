<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${errorTitle}"/>
<%@ include file="header.jspf" %>
<header class="page-head">
    <h1>${errorTitle}</h1>
</header>
<div class="notice notice-error" role="alert">${errorMessage}</div>
<p><a class="btn btn-quiet" href="<c:url value='/'/>">Back to overview</a></p>
<%@ include file="footer.jspf" %>
