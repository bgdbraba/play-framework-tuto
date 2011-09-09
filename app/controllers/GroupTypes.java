package controllers;

import models.GroupType;
import play.mvc.With;
import controllers.CRUD.For;

@With(Secure.class)
@For(GroupType.class)
public class GroupTypes extends AbstractController {

}