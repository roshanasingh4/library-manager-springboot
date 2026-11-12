<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Overview"/>
<%@ include file="common/header.jspf" %>
<header class="page-head">
    <h1>Overview</h1>
    <p class="lede">The collection at a glance. Add authors, catalogue their books and keep the records up to date.</p>
</header>

<section class="stats" aria-label="Collection totals">
    <a class="stat" href="<c:url value='/authors'/>">
        <span class="stat-value">${authorCount}</span>
        <span class="stat-label">Authors</span>
    </a>
    <a class="stat" href="<c:url value='/books'/>">
        <span class="stat-value">${bookCount}</span>
        <span class="stat-label">Books</span>
    </a>
    <div class="stat">
        <span class="stat-value">${genreCount}</span>
        <span class="stat-label">Genres</span>
    </div>
</section>

<section class="panel">
    <div class="panel-head">
        <h2>Authors with books in the collection</h2>
        <p class="muted">Inner join of authors and books, ranked by the number of books each has written.</p>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Author</th>
            <th scope="col">Country</th>
            <th scope="col" class="num">Books</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="a" items="${topAuthors}">
            <tr>
                <td><a class="row-link" href="<c:url value='/authors/${a.id}/edit'/>">${a.name}</a></td>
                <td>${a.country}</td>
                <td class="num">${a.bookCount}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
<%@ include file="common/footer.jspf" %>
