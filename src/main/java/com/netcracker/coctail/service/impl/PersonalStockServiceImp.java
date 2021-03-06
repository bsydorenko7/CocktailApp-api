package com.netcracker.coctail.service.impl;

import com.netcracker.coctail.dao.FriendlistDao;
import com.netcracker.coctail.dao.IngredientDao;
import com.netcracker.coctail.dao.StockIngredientDao;
import com.netcracker.coctail.model.StockIngredientInfo;
import com.netcracker.coctail.model.StockIngredient;
import com.netcracker.coctail.security.jwt.JwtTokenProvider;
import com.netcracker.coctail.service.PersonalStockService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Data
public class PersonalStockServiceImp implements PersonalStockService {

    private final StockIngredientDao stockIngredientDao;
    private final FriendlistDao friendlistDao;
    private final IngredientDao ingredientDao;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean addIngredientToStock(long userId, long ingredientId, long quantity) {
        if (ingredientDao.findIngredientById(ingredientId).get(0) == null) {
            log.warn("IN addIngredientTOStock - no ingredient found by id: {}", ingredientId);
            return false;
        } else if (!stockIngredientDao.findExistingStockIngredientById(userId, ingredientId)
                .isEmpty()) {
            log.warn("IN addIngredientTOStock - user with id " + userId + " has already ingredient with id " + ingredientId
                    + " in stock");
            return false;
        } else {
            stockIngredientDao.addIngredientToStock(userId, ingredientId, quantity);
            return true;
        }
    }

    @Override
    public boolean editIngredient(long userId, long ingredientId, long quantity) {
        List<StockIngredient> stockIngredients = getExistingStockIngredientById(userId, ingredientId);
        if (ingredientDao.findIngredientById(ingredientId).get(0) == null) {
            log.warn("IN editIngredient - no ingredient found by id: {}", ingredientId);
            return false;
        } else if (stockIngredients.isEmpty()) {
            log.warn("IN editIngredient - user with id " + userId + " doesn't have ingredient with id " + ingredientId);
            return false;
        } else {
            stockIngredientDao.editStockIngredient(stockIngredients.get(0).getId(), quantity);
            return true;
        }
    }

    @Override
    public boolean removeIngredientFromStock(long userId, long ingredientId) {
        List<StockIngredient> stockIngredients = getExistingStockIngredientById(userId, ingredientId);
        if (ingredientDao.findIngredientById(ingredientId).get(0) == null) {
            log.warn("IN removeIngredientFromStock - no ingredient found by id: {}", ingredientId);
            return false;
        } else if (stockIngredients.isEmpty()) {
            log.warn("IN removeIngredientFromStock - user with id " + userId + " doesn't have ingredient with id " + ingredientId);
            return false;
        } else {
            stockIngredientDao.removeIngredientFromStock(stockIngredients.get(0).getId());
            return true;
        }
    }

    @Override
    public long getOwnerIdByToken(String token) {
        String email = jwtTokenProvider.getEmail(token.substring(7));
        return friendlistDao.getOwnerId(email);
    }

    @Override
    public List<StockIngredientInfo> getStockIngredientsByName(long userId, String name) {
        return stockIngredientDao.findStockIngredientsByName(userId, name);
    }

    @Override
    public List<StockIngredient> getExistingStockIngredientById(long userId, long ingredientId) {
        return stockIngredientDao.findExistingStockIngredientById(userId, ingredientId);
    }

    @Override
    public List<StockIngredientInfo> getStockIngredientsFiltered(long userId, String type,
                                                                 String category) {
        return stockIngredientDao.findStockIngredientsFiltered(userId, type, category);
    }

}
