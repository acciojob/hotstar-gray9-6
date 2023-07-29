package com.driver.services;


import com.driver.exception.UserDoesNotExists;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()){
            throw new UserDoesNotExists("Invalid User Id");
        }

        // if it exists , then get the user
        User user = userOptional.get();

        // get the list of all the webSeries
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();

        Integer seriesCount = 0;
        for (WebSeries webSeries : webSeriesList){
            if(user.getAge() >= webSeries.getAgeLimit()){
                seriesCount ++;
            }
        }


        return seriesCount;
    }


}
