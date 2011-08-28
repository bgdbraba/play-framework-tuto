package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

public enum QuestionType {

	MULTIPLE_CHOICE, SIMPLE_CHOICE, TEXT_ANSWER;
}
