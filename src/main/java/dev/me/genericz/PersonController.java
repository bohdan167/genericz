package dev.me.genericz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("persons")
class PersonController {

    private final PersonRepository personRepository;

    private PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}")
    private ResponseEntity<Person> findById(@PathVariable Long id, Principal principal) {
        Optional<Person> person = findPerson(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Person> createPerson(
            @RequestBody Person newPersonRequested,
            UriComponentsBuilder ucb
    ) {
        Person savedPerson = personRepository.save(newPersonRequested);
        URI locationOfNewPerson = ucb
                .path("/persons/{id}")
                .buildAndExpand(savedPerson.id())
                .toUri();
        return ResponseEntity.created(locationOfNewPerson).build();
    }

    @GetMapping
    private ResponseEntity<List<Person>> findAll(Pageable pageable) {
        Page<Person> page = personRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"))
        ));
        return ResponseEntity.ok(page.getContent());
    }

    private Optional<Person> findPerson(Long id) {
        return personRepository.findById(id);
    }
}
