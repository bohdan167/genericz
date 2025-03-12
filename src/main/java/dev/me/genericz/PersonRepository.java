package dev.me.genericz;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface PersonRepository extends
        CrudRepository<Person, Long>
{
    Page<Person> findAll(Pageable pageable);
}
