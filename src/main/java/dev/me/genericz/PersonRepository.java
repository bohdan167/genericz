package dev.me.genericz;


import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface PersonRepository extends
        CrudRepository<Person, Long>
{
    Person findByIdAndUsername(Long id, String username);
}
