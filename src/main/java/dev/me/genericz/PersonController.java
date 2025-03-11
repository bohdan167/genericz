package dev.me.genericz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/persons")
class PersonController {

    private final PersonRepository personRepository;

    private PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<Person> findById(@PathVariable Long id, Principal principal) {
        Person person = findPerson(id, principal);
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Person findPerson(Long id, Principal principal) {
        return personRepository.findByIdAndUsername(id, principal.getName());
    }
}
