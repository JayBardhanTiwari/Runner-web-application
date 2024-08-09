package com.jay.runner.runnerwebapp.run;


import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

public interface  Runrepository  extends ListCrudRepository<Run,Integer>{
    

    List<Run> findAllByLocation(String location);
}
