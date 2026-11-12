<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Authors"/>
<%@ include file="../common/header.jspf" %>
<header class="page-head page-head-row">
    <div>
        <h1>Authors</h1>
        <p class="lede">${authors.size()} authors on record, in alphabetical order.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/authors/new'/>">Add author</a>
</header>

<section class="panel">
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">E-mail</th>
            <th scope="col">Country</th>
            <th scope="col" class="num">Born</th>
            <th scope="col" class="num">Books</th>
            <th scope="col"><span class="visually-hidden">Actions</span></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${authors}">
            <tr>
                <td class="strong">${a.name}</td>
                <td class="muted">${a.email}</td>
                <td>${a.country}</td>
                <td class="num">${a.birthYear}</td>
                <td class="num">${a.bookCount}</td>
                <td class="actions">
                    <a class="link" href="<c:url value='/authors/${a.id}/edit'/>">Edit</a>
                    <a class="link" href="<c:url value='/books/new?authorId=${a.id}'/>">Add book</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty authors}">
            <tr><td colspan="6" class="empty">No authors yet. Add the first author to start the catalogue.</td></tr>
        </c:if>
        </tbody>
    </table>
</section>
<%@ include file="../common/footer.jspf" %>
