package com.netcracker.coctail.controllers;

import com.netcracker.coctail.dao.UserDao;
import com.netcracker.coctail.dto.UserDto;

import com.netcracker.coctail.model.DishRecipe;
import com.netcracker.coctail.model.FriendUser;
import com.netcracker.coctail.model.StockIngredientInfo;
import com.netcracker.coctail.model.StockIngredientOperations;
import com.netcracker.coctail.model.User;
import com.netcracker.coctail.model.EventInfo;
import com.netcracker.coctail.model.Event;
import com.netcracker.coctail.model.CreateEvent;
import com.netcracker.coctail.model.UserPersonalInfo;

import com.netcracker.coctail.security.jwt.JwtTokenProvider;



import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.netcracker.coctail.service.FriendlistService;
import com.netcracker.coctail.service.IngredientService;
import com.netcracker.coctail.service.PersonalStockService;
import com.netcracker.coctail.service.RecipeService;
import com.netcracker.coctail.service.UserService;
import com.netcracker.coctail.service.EventService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller user connected requests.
 */

@RestController
@RequestMapping(value = "/api/users/")
@CrossOrigin(origins = "*")
@Data
public class UserRestController {
  private UserService userService;
  private JwtTokenProvider jwtTokenProvider;
  private UserDao userDao;
  private FriendlistService friendlistService;
  private PasswordEncoder passwordEncoder;
  private RecipeService recipeService;
  private PersonalStockService personalStockService;
  private IngredientService ingredientService;
  private EventService eventService;

    @Autowired
    @Lazy
    public UserRestController(UserService userService,
                              JwtTokenProvider jwtTokenProvider,
                              UserDao userDao,
                              FriendlistService friendlistService,
                              PasswordEncoder passwordEncoder,
                              RecipeService recipeService,
                              PersonalStockService personalStockService,
                              EventService eventService,
                              IngredientService ingredientService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.friendlistService = friendlistService;
        this.passwordEncoder = passwordEncoder;
        this.recipeService = recipeService;
        this.personalStockService = personalStockService;
        this.eventService = eventService;
        this.ingredientService = ingredientService;
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        UserDto result = UserDto.fromUser(user);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PutMapping(value = "settings/edit")
    public ResponseEntity<?> editMyPersonalData(HttpServletRequest request,
                                                @RequestBody @Valid UserPersonalInfo user) {
        String email = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        userService.changeInfo(email, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

  @PostMapping("add/{friendid}")
  public ResponseEntity addFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    Boolean ret = friendlistService.addFriend(ownerEmail, friendid);
    return ret == Boolean.TRUE
        ? new ResponseEntity(ret, HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
  }

  @GetMapping("find")
  public ResponseEntity<List<FriendUser>> getUserByNickname(@RequestParam String nickname,
                                                            HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    List<FriendUser> users = friendlistService.getUserByNickname(ownerEmail, nickname);
    if (users.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PatchMapping("accept/{friendid}")
  public ResponseEntity acceptFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    Boolean ret = friendlistService.acceptFriendRequest(ownerEmail, friendid);
    return ret == Boolean.TRUE
        ? new ResponseEntity(ret, HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
  }

  @PatchMapping("decline/{friendid}")
  public ResponseEntity declineFriend(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    Boolean ret = friendlistService.declineFriendRequest(ownerEmail, friendid);
    return ret == Boolean.TRUE
        ? new ResponseEntity(ret, HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
  }

    @PatchMapping("subscribe/{friendid}")
    public ResponseEntity subcribeTo(
            @PathVariable(name = "friendid") long friendid,
            @RequestHeader("Authorization") String token) {
        String ownerEmail = jwtTokenProvider.getEmail(token.substring(7));
        Boolean ret = friendlistService.subscribeToFriend(ownerEmail, friendid);
        return ret == Boolean.TRUE
                ? new ResponseEntity(ret, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @PatchMapping("unsubscribe/{friendid}")
    public ResponseEntity unsubcribeFrom(
            @PathVariable(name = "friendid") long friendid,
            @RequestHeader("Authorization") String token) {
        String ownerEmail = jwtTokenProvider.getEmail(token.substring(7));
        Boolean ret = friendlistService.unsubcribeFromFriend(ownerEmail, friendid);
        return ret == Boolean.TRUE
                ? new ResponseEntity(ret, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

  @DeleteMapping("remove/{friendid}")
  public ResponseEntity removeFromFriends(
      @PathVariable(name = "friendid") long friendid,
      HttpServletRequest request) {
    String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
    Boolean ret = friendlistService.removeFriend(ownerEmail, friendid);
    return ret == Boolean.TRUE
        ? new ResponseEntity(ret, HttpStatus.OK) :
        new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
  }

    @GetMapping("recipe")
    public ResponseEntity<List<DishRecipe>> getRecipeByName(@RequestParam String name) {
        List<DishRecipe> recipes = recipeService.getRecipesByName(name);
        if (recipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("recipe/filter")
    public ResponseEntity<List<DishRecipe>> getRecipesFiltered(
            @RequestParam boolean sugarless, @RequestParam String alcohol) {
        List<DishRecipe> recipes = recipeService.getRecipesFiltered(sugarless, alcohol);
        if (recipes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

  @PostMapping(value = "stock/ingredients")
  public void addIngredient(@RequestHeader("Authorization") String token, @RequestBody
      StockIngredientOperations stockIngredientOperations) {
    long userId = personalStockService.getOwnerIdByToken(token);
    personalStockService.addIngredientToStock(userId, stockIngredientOperations);
  }

    @DeleteMapping("stock/remove/{ingredientid}")
    public void removeStockIngredient(
            @RequestHeader("Authorization") String token,
            @PathVariable(name = "ingredientid") long ingredientId) {
        long userId = personalStockService.getOwnerIdByToken(token);
        personalStockService.removeIngredientFromStock(userId, ingredientId);
    }

    @PatchMapping(value = "stock/edit")
    public void editStockIngredient(
            @RequestHeader("Authorization") String token,
            @RequestBody StockIngredientOperations stockIngredientOperations) {
        long userId = personalStockService.getOwnerIdByToken(token);
        personalStockService.editIngredient(userId, stockIngredientOperations);
    }

    @GetMapping(value = "stock/my")
    public ResponseEntity<List<StockIngredientInfo>> getAllUserIngredients(@RequestHeader("Authorization") String token) {
        long userId = personalStockService.getOwnerIdByToken(token);
        List<StockIngredientInfo> stockIngredients = personalStockService.getStockIngredientsByName(userId, "");
        if (stockIngredients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(stockIngredients, HttpStatus.OK);
    }

    @GetMapping("stock/search")
    public ResponseEntity<List<StockIngredientInfo>> getStockIngredientsByName(@RequestHeader("Authorization") String token,
                                                                               @RequestParam String name) {
        long userId = personalStockService.getOwnerIdByToken(token);
        List<StockIngredientInfo> stockIngredients = personalStockService.getStockIngredientsByName(userId, name);
        if (stockIngredients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(stockIngredients, HttpStatus.OK);
    }

  @GetMapping("stock/filter")
  public ResponseEntity<List<StockIngredientInfo>> getStockIngredientsFiltered(
      @RequestHeader("Authorization") String token, @RequestParam String type,
      @RequestParam String category) {
    long userId = personalStockService.getOwnerIdByToken(token);
    List<StockIngredientInfo> stockIngredients = personalStockService.getStockIngredientsFiltered(userId, type, category);
    if (stockIngredients.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(stockIngredients, HttpStatus.OK);
  }

  @GetMapping("recipe/list")
  public ResponseEntity<List<DishRecipe>> recipesList() {
    List<DishRecipe> recipes = recipeService.getRecipesByName("");
    if (recipes.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(recipes, HttpStatus.OK);
  }

    @GetMapping(value = "recipe/{id}")
    public ResponseEntity<DishRecipe> getRecipeById(@PathVariable(name = "id") int id) {
        DishRecipe result = recipeService.getRecipeById(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping(value = "recipe/favourites/{id}")
    public ResponseEntity addToFavourites(@PathVariable(name = "id") int id, @RequestParam boolean favourite, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (recipeService.addToFavourites(ownerEmail, id, favourite)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @PatchMapping("recipe/{id}")
    public ResponseEntity likeRecipe(
            @PathVariable(name = "id") int id,
            @RequestParam boolean like,
            HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (recipeService.likeRecipe(ownerEmail, id, like)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @GetMapping(value = "events/{id}")
    public ResponseEntity<EventInfo> eventInfo(@PathVariable(name = "id") int id) {
        EventInfo result = eventService.eventInfo(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "events/list")
    public ResponseEntity<List<Event>> eventsList() {
        List<Event> events = eventService.getEventsByName("");
        if (events.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping(value = "events/filter")
    public ResponseEntity<List<Event>> getEventsFiltered(HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        List<Event> events = eventService.getEventsFiltered(ownerEmail);
        if (events.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping(value = "events")
    public ResponseEntity<Integer> createEvent(@RequestBody CreateEvent event, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        int id = eventService.createEvent(ownerEmail, event);
        if (id > 0) {
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "join/{id}")
    public ResponseEntity joinEvent(@PathVariable(name = "id") int id, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.joinEvent(ownerEmail, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "events/{id}/recipe")
    public ResponseEntity addRecipeToEvent(@PathVariable(name = "id") int id, @RequestParam String name, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.addRecipeToEvent(id, name, ownerEmail)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping(value = "leave/{id}")
    public ResponseEntity leaveEvent(@PathVariable(name = "id") int id, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.leaveEvent(ownerEmail, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping(value = "events/{id}/recipe")
    public ResponseEntity removeRecipeFromEvent(@PathVariable(name = "id") int id, @RequestParam String name, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.removeRecipeFromEvent(id, name, ownerEmail)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PatchMapping(value = "events/{id}")
    public ResponseEntity editEvent(@RequestBody CreateEvent event, @PathVariable(name = "id") int id, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.editEvent(ownerEmail, event, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping(value = "events/{id}")
    public ResponseEntity declineEvent(@PathVariable(name = "id") int id, HttpServletRequest request) {
        String ownerEmail = jwtTokenProvider.getEmail(request.getHeader("Authorization").substring(7));
        if (eventService.declineEvent(ownerEmail, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }


}
