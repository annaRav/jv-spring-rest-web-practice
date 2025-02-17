package mate.academy.spring.security;

import java.util.Optional;
import mate.academy.spring.exception.AuthenticationException;
import mate.academy.spring.model.User;
import mate.academy.spring.service.ShoppingCartService;
import mate.academy.spring.service.UserService;
import mate.academy.spring.util.HashUtil;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ShoppingCartService shoppingCartService;

    public AuthenticationServiceImpl(UserService userService,
                                     AuthenticationService authenticationService,
                                     ShoppingCartService shoppingCartService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userFromDb = userService.findByEmail(email);
        if (userFromDb.isPresent() && matchPasswords(password, userFromDb.get())) {
            return userFromDb.get();
        }
        throw new AuthenticationException("Incorrect email or password!");
    }

    @Override
    public User register(String email, String password) {
        User registeredUser = authenticationService.register(email, password);
        shoppingCartService.registerNewShoppingCart(registeredUser);
        return registeredUser;
    }

    private boolean matchPasswords(String rawPassword, User userFromDb) {
        String hashedPassword = HashUtil.hashPassword(rawPassword, userFromDb.getSalt());
        return hashedPassword.equals(userFromDb.getPassword());
    }
}
