<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Books"/>
<%@ include file="../common/header.jspf" %>
<header class="page-head page-head-row">
    <div>
        <h1>Books</h1>
        <p class="lede">Each book joined with its author through an inner join query.</p>
    </div>
    <a class="btn btn-primary" href="<c:url value='/books/new'/>">Add book</a>
</header>

<form class="filter" method="get" action="<c:url value='/books'/>">
    <label for="genre">Genre</label>
    <select id="genre" name="genre" onchange="this.form.submit()">
        <option value="">All genres</option>
        <c:forEach var="g" items="${genres}">
            <option value="${g}" ${g == selectedGenre ? 'selected' : ''}>${g}</option>
        </c:forEach>
    </select>
    <noscript><button type="submit" class="btn btn-quiet">Apply</button></noscript>
    <span class="muted">${rows.size()} ${rows.size() == 1 ? 'book' : 'books'}</span>
</form>

<section class="panel">
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Title</th>
            <th scope="col">Author</th>
            <th scope="col">Genre</th>
            <th scope="col">ISBN</th>
            <th scope="col" class="num">Year</th>
            <th scope="col" class="num">Price (INR)</th>
            <th scope="col"><span class="visually-hidden">Actions</span></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="row" items="${rows}">
            <tr>
                <td class="strong">${row.title}</td>
                <td>${row.authorName}<span class="sub">${row.authorCountry}</span></td>
                <td><span class="tag">${row.genre}</span></td>
                <td class="mono">${row.isbn}</td>
                <td class="num">${row.publishedYear}</td>
                <td class="num"><fmt:formatNumber value="${row.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="actions"><a class="link" href="<c:url value='/books/${row.bookId}/edit'/>">Edit</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty rows}">
            <tr><td colspan="7" class="empty">No books match this genre. Choose another genre or add a book.</td></tr>
        </c:if>
        </tbody>
    </table>
</section>
<%@ include file="../common/footer.jspf" %>
