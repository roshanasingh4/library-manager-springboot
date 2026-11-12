<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${editing ? 'Edit author' : 'Add author'}"/>
<%@ include file="../common/header.jspf" %>
<header class="page-head">
    <a class="back" href="<c:url value='/authors'/>">Authors</a>
    <h1>${editing ? 'Edit author' : 'Add author'}</h1>
    <p class="lede">${editing ? 'Change the details below and save. The e-mail address must stay unique.' : 'Every author needs a unique e-mail address.'}</p>
</header>

<c:url var="action" value="${editing ? '/authors/'.concat(author.id) : '/authors'}"/>
<form:form modelAttribute="author" action="${action}" method="post" cssClass="panel form" novalidate="novalidate">
    <form:errors path="*" element="div" cssClass="notice notice-error" delimiter="<br>"/>

    <div class="field">
        <form:label path="name">Full name</form:label>
        <form:input path="name" cssErrorClass="invalid" placeholder="e.g. Ruskin Bond"/>
        <form:errors path="name" cssClass="field-error"/>
    </div>

    <div class="field">
        <form:label path="email">E-mail</form:label>
        <form:input path="email" type="email" cssErrorClass="invalid" placeholder="name@example.org"/>
        <form:errors path="email" cssClass="field-error"/>
    </div>

    <div class="field-row">
        <div class="field">
            <form:label path="country">Country</form:label>
            <form:input path="country" cssErrorClass="invalid" placeholder="e.g. India"/>
            <form:errors path="country" cssClass="field-error"/>
        </div>
        <div class="field">
            <form:label path="birthYear">Birth year</form:label>
            <form:input path="birthYear" type="number" cssErrorClass="invalid" placeholder="1934"/>
            <form:errors path="birthYear" cssClass="field-error"/>
        </div>
    </div>

    <div class="form-actions">
        <button type="submit" class="btn btn-primary">${editing ? 'Save changes' : 'Add author'}</button>
        <a class="btn btn-quiet" href="<c:url value='/authors'/>">Cancel</a>
    </div>
</form:form>
<%@ include file="../common/footer.jspf" %>
