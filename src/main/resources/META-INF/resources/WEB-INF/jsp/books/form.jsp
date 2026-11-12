<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editing ? 'Edit book' : 'Add book'}"/>
<%@ include file="../common/header.jspf" %>
<header class="page-head">
    <a class="back" href="<c:url value='/books'/>">Books</a>
    <h1>${editing ? 'Edit book' : 'Add book'}</h1>
    <p class="lede">${editing ? 'Change the details below and save. The ISBN must stay unique.' : 'Choose the author and enter the book details. Each ISBN can be used only once.'}</p>
</header>

<c:url var="action" value="${editing ? '/books/'.concat(book.id) : '/books'}"/>
<form:form modelAttribute="book" action="${action}" method="post" cssClass="panel form" novalidate="novalidate">
    <form:errors path="*" element="div" cssClass="notice notice-error" delimiter="<br>"/>

    <div class="field">
        <form:label path="title">Title</form:label>
        <form:input path="title" cssErrorClass="invalid" placeholder="e.g. The Blue Umbrella"/>
        <form:errors path="title" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="authorId">Author</form:label>
        <form:select path="authorId" cssErrorClass="invalid">
            <form:option value="" label="Select an author"/>
            <form:options items="${authors}" itemValue="id" itemLabel="name"/>
        </form:select>
        <form:errors path="authorId" cssClass="field-error"/>
    </div>

    <div class="field-row">
        <div class="field">
            <form:label path="isbn">ISBN</form:label>
            <form:input path="isbn" cssErrorClass="invalid" placeholder="978-0123456789"/>
            <form:errors path="isbn" cssClass="field-error"/>
        </div>
        <div class="field">
            <form:label path="genre">Genre</form:label>
            <form:input path="genre" cssErrorClass="invalid" placeholder="e.g. Fiction" list="genre-list"/>
            <datalist id="genre-list">
                <option value="Classic"><option value="Dystopian"><option value="Fiction">
                <option value="Magical Realism"><option value="Short Stories"><option value="Non-fiction">
            </datalist>
            <form:errors path="genre" cssClass="field-error"/>
        </div>
    </div>

    <div class="field-row">
        <div class="field">
            <form:label path="publishedYear">Published year</form:label>
            <form:input path="publishedYear" type="number" cssErrorClass="invalid" placeholder="1980"/>
            <form:errors path="publishedYear" cssClass="field-error"/>
        </div>
        <div class="field">
            <form:label path="price">Price (INR)</form:label>
            <form:input path="price" type="number" step="0.01" cssErrorClass="invalid" placeholder="299.00"/>
            <form:errors path="price" cssClass="field-error"/>
        </div>
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">${editing ? 'Save changes' : 'Add book'}</button>
        <a class="btn btn-quiet" href="<c:url value='/books'/>">Cancel</a>
    </div>
</form:form>
<%@ include file="../common/footer.jspf" %>
