package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.CollegeSubreddit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegeSubredditRepository extends CrudRepository<CollegeSubreddit, Long> {
    Iterable<CollegeSubreddit> findByName(String name);
    Iterable<CollegeSubreddit> findBySubreddit(String subreddit);
}
