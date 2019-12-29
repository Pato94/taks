package tacs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

    private String TOKEN_SUFFIX = "tokensecreto";
    private List<Account> accounts = new LinkedList<>();

    @GetMapping("/")
    public String home(@CookieValue(value = "token", defaultValue = "") String token) {
        String username = usernameFromToken(token);
        if (accounts.stream().noneMatch(account -> account.getUsername().equals(username))) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response, @RequestParam("username") String username, @RequestParam("password") String password) {
        Optional<Account> maybeAccount = accounts.stream().filter(account -> account.getUsername().equals(username)).findFirst();
        if (maybeAccount.isPresent()) {
            Account account = maybeAccount.get();
            if (password.equals(account.getPassword())) {
                response.addCookie(new Cookie("token", username + TOKEN_SUFFIX));
                return "redirect:/";
            }
        }
        return "redirect:/error";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password) {
        Account newAccount = new Account(username, password);
        if (accounts.stream().anyMatch(account -> account.getUsername().equals(username))) {
            return "redirect:/error"; // TODO: Add better error message
        }
        accounts.add(newAccount);
        return "redirect:/login";
    }

    private String usernameFromToken(@CookieValue(value = "token", defaultValue = "") String token) {
        return token.replaceAll(TOKEN_SUFFIX, "");
    }
}