package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.exception.AlreadyHaveBestSubscription;
import com.driver.exception.UserDoesNotExists;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Optional<User> userOptional = userRepository.findById(subscriptionEntryDto.getUserId());
        if(!userOptional.isPresent()){
            throw new UserDoesNotExists("Invalid User id");
        }

        // else get the user
        User user = userOptional.get();

        // now convert the dto to subscription
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        Integer planCost = 0;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            planCost = 500 + 200*(subscriptionEntryDto.getNoOfScreensRequired());
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            planCost = 800 + 250*(subscriptionEntryDto.getNoOfScreensRequired());
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE)) {
            planCost = 1000 + 350*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(planCost);
        subscription.setUser(user);
        user.setSubscription(subscription);

        // now save in the database
        userRepository.save(user);

        return planCost ;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent()){
            throw new UserDoesNotExists("Invalid User id");
        }

        // if it exists then get the user
        User user = userOptional.get();

        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
        if(subscriptionType.equals(SubscriptionType.ELITE)){
            throw new AlreadyHaveBestSubscription("Already the best Subscription");
        }

        int amountBeforeUpgrade = user.getSubscription().getTotalAmountPaid();

        // if not then upgrade the subscription
        if(subscriptionType.equals(SubscriptionType.BASIC)){
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            int planCost = 800 + 250*(user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setTotalAmountPaid(planCost);
        } else if (subscriptionType.equals(SubscriptionType.PRO)) {
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            int planCost = 1000 + 350*(user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setTotalAmountPaid(planCost);
        }

        // save the use to db , to save the changes
        User savedUser = userRepository.save(user);

        int amountAfterUpgrade = savedUser.getSubscription().getTotalAmountPaid();

        int amountDifference = amountAfterUpgrade - amountBeforeUpgrade;

        return amountDifference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        if(subscriptionList.isEmpty()){
            throw new RuntimeException("Subscription List is Empty");
        }

        Integer totalRevenue = 0;
        for (Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
