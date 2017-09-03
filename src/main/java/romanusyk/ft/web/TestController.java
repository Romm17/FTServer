package romanusyk.ft.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import romanusyk.ft.domain.Group;
import romanusyk.ft.domain.Payment;
import romanusyk.ft.domain.PaymentDTO;
import romanusyk.ft.domain.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import romanusyk.ft.repository.Optimizer;
import romanusyk.ft.security.JwtAccessToken;
import romanusyk.ft.security.JwtUtil;
import romanusyk.ft.service.PaymentService;
import romanusyk.ft.service.SpringUserService;
import romanusyk.ft.service.UserService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by romm on 01.02.17.
 */

@CrossOrigin
@RestController
@Api("Test controller")
@RequestMapping("/api/v1")
public class TestController {

//    @Autowired
//    @Qualifier("simpleUserServiceBean")
//    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    @Qualifier("springUserServiceBean")
    private UserService userService;

    @Autowired
    private Optimizer optimizer;

    private static final Logger logger = Logger.getLogger(TestController.class);

    /**
     * Sanity check
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        return "Hello!";
    }

    /**
     * Demo init database
     * @return
     */
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {
        userService.init();
        paymentService.init();
        return "Initialized!";
    }

    @ApiOperation(
            value = "get all users",
            notes = "get all all all users",
            produces = "Application/json"
    )
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserById() {
        List<User> users = userService.getAllUsers();
        if (users == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@PathVariable("id") Integer id) {
        User user = userService.getUserByID(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/groups", method = RequestMethod.GET)
    public ResponseEntity<Set<Group>> getUserById(@RequestHeader("Authorization") String authorization) {
        String tokenValue = getTokenValue(authorization);
        User u = JwtUtil.parseToken(tokenValue);
        logger.debug(u);
        if (u == null || u.getId() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!checkPermissions(u.getId(), authorization)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        u = userService.getUserByID(u.getId());
        return new ResponseEntity<>(u.getGroups(), HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody @Valid User user,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> response = new HashMap<>();
            for (ObjectError e : bindingResult.getAllErrors()) {
                response.put(e.getObjectName(), e.toString());
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        User user1 = userService.getUserByUsername(user.getUsername());
        if (user1 != null) {
            Map<String, String> response = new HashMap<>();
            response.put("username", String.format("User with username '%s' already exists!", user1.getUsername()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        logger.info("Persisting user : " + user);
        userService.createUser(user);
        return new ResponseEntity<>(JwtUtil.getInstance().getToken(user), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users", method = RequestMethod.PATCH)
    public ResponseEntity<JwtAccessToken> valdateUser(@RequestBody User user) {
        logger.info("Validating user : " + user);
        user = userService.validateUser(user);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(JwtUtil.getInstance().getToken(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/user_debts", method = RequestMethod.GET)
    public ResponseEntity<Map<Integer, BigDecimal>> getUserDebts(@RequestHeader("Authorization") String authorization)
    {
        String tokenValue = getTokenValue(authorization);
        User u = JwtUtil.parseToken(tokenValue);
        logger.debug(u);
        if (u == null || u.getId() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!checkPermissions(u.getId(), authorization)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Map<Integer, BigDecimal> result = paymentService.getUserPayments(u.getId());
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/user_debts/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<PaymentDTO>> getDebtsBetweenUsers(
            @RequestHeader("Authorization") String authorization,
            @PathVariable("id") Integer userToID
    ) {
        String tokenValue = getTokenValue(authorization);
        User u = JwtUtil.parseToken(tokenValue);
        logger.debug(u);
        if (u == null || u.getId() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!checkPermissions(u.getId(), authorization)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Payment> payments = paymentService.getPaymentsBetweenUsers(u.getId(), userToID);
        if (payments == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<PaymentDTO> result = new ArrayList<>(payments.size());
        for (Payment p : payments) {
            result.add(
                    new PaymentDTO(
                            p.getUserFrom().getId(),
                            new Integer[]{p.getUserTo().getId()},
                            p.getAmount(),
                            p.getDescription(),
                            p.getDate(),
                            p.getLongitude(),
                            p.getLatitude()
                    )
            );
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public ResponseEntity<Boolean> makeGroupPayment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PaymentDTO paymentDTO
    ) {
        String tokenValue = getTokenValue(authorization);
        User u = JwtUtil.parseToken(tokenValue);
        logger.debug(u);
        if (u == null || u.getId() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        u = userService.getUserByID(u.getId());
        if (!checkPermissions(u.getId(), authorization)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        paymentDTO.setUserFrom(u.getId());
        boolean success = false;
        paymentDTO.validate();
        try{
            success = paymentService.makeGroupPayment(paymentDTO);
        } catch (RuntimeException e) {
            logger.error("Runtime exception while adding group payment!");
            logger.error(e);
        }
        if (success) {
            logger.info(
                    "Transaction succeed! "
                            + "User with id = " + paymentDTO.getUserFrom()
                            + " payed for users with id = " + Arrays.toString(paymentDTO.getUsersTo())
                            + " " + paymentDTO.getAmount() + " hryvnas."
            );
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            logger.error("Transaction failed from user " + paymentDTO.getUserFrom() + " to users " + Arrays.toString(paymentDTO.getUsersTo()));
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "optimize", method = RequestMethod.GET)
    public String optimize() {
        optimizer.calculateDebts();
        return "Optimized!";
    }

    public static String getTokenValue(String authorizationHeader) {
        return authorizationHeader.split(" ")[1];
    }

    public static boolean checkPermissions(Integer userID, String authorizationHeader) {
        JwtUtil jwtUtil = JwtUtil.getInstance();
        String token = authorizationHeader.split(" ")[1];
        return jwtUtil.hasAccess(userID, token);
    }
}