package com.juyoung.restapiwithspring.error;

import com.juyoung.restapiwithspring.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class ErrorsResource2 extends Resource<ErrorResponse> {

    ErrorsResource2(ErrorResponse content, Link... links) {
        super(content, links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
