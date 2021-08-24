package com.jaimedantas.fiitaxcalculator.storage;

import com.jaimedantas.fiitaxcalculator.model.FiiTax;
import com.jaimedantas.fiitaxcalculator.model.UserStocks;

import java.util.LinkedList;
import java.util.Optional;

public class StaticStorage {

    private StaticStorage(){}

    private static LinkedList<UserStocks> userStocks = new LinkedList<>();

    public static void addStock(int id, FiiTax fiiTax){

        Optional<UserStocks> existingUserStocks;


        existingUserStocks = userStocks.stream()
                .filter( x -> x.getUserId() == id)
                .findAny();

        if(existingUserStocks.isPresent()){

            existingUserStocks.get().getStocksList().add(fiiTax);
            userStocks.removeIf(x -> x.getUserId() == id);
            userStocks.add(existingUserStocks.get());

        } else {

            LinkedList<FiiTax> fiiTaxList = new LinkedList<>();
            fiiTaxList.add(fiiTax);
            userStocks.add(new UserStocks(id, fiiTaxList));

        }


    }

    public static LinkedList<FiiTax> getStock(int id){

        Optional<UserStocks> existingUserStocks;

         existingUserStocks = Optional.of(userStocks.stream()
                 .filter(x -> x.getUserId() == id)
                 .findAny().orElse(new UserStocks()));

         return existingUserStocks.get().getStocksList();
    }

}
