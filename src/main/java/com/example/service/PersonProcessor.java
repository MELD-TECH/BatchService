package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.model.Person;

public class PersonProcessor implements ItemProcessor<Person, Person> {

	Logger log = LoggerFactory.getLogger(PersonProcessor.class);
	
	@Override
	public Person process(Person item) throws Exception {
		// TODO Auto-generated method stub
		

		String lastname = item.getLastname().toUpperCase();
		String firstname = item.getFirstname().toLowerCase();
		
		Person modifiedPerson = new Person(lastname, firstname);
		
		log.info("Converting (" + item + ") into (" + modifiedPerson + ")");
		
		return modifiedPerson;
	}

}
