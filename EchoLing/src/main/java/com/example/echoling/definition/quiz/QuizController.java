package com.example.echoling.definition.quiz;

import com.example.echoling.auntefication.CookieUtils;
import com.example.echoling.data.User.User;
import com.example.echoling.data.User.UserRepository;
import com.example.echoling.data.User.UserService;
import com.example.echoling.definition.Parser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@SessionAttributes("word")
public class QuizController {


    private final UserRepository userRepository;
    private final QuizService quizService;
    private final UserService userService;
    private final CookieUtils cookieUtils;

    public QuizController(UserRepository userRepository, QuizService quizService, UserService userService, CookieUtils cookieUtils) {
        this.userRepository = userRepository;
        this.quizService = quizService;
        this.userService = userService;
        this.cookieUtils = cookieUtils;
    }

    @GetMapping("/quiz")
    public String learningWords(Model model, HttpServletRequest request, Principal principal, HttpServletRequest httpServletRequest) {

        if (principal == null)
            return "redirect:/login";
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return "redirect:/login";

        request.setAttribute("user", user);
        try {
            model.addAttribute("currentLanguage", cookieUtils.getCookie(httpServletRequest, "echoLingCurrentLanguage").get());
        } catch (Exception e) {
            model.addAttribute("currentLanguage", "English");
        }

        return "quiz_page";
    }

    @PostMapping("/quiz")
    public String ProceedDefinition(@RequestParam String sourceLang, Model model, HttpServletRequest request,
                                    RedirectAttributes redirectAttributes, Principal principal, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        model.addAttribute("error", "");

        if (principal == null)
            return "redirect:/login";
        String username = principal.getName();
        User user = userService.authenticate(username);
        if (user == null)
            return "redirect:/login";


        String word;
        try {
            word = FetchWord.fetchWord(cookieUtils.getCookie(httpServletRequest, "echoLingCurrentLanguage").get());
        } catch (Exception e) {
            word = FetchWord.fetchWord("English");
        }
        model.addAttribute("word", word);
        try{
        var wordDefinition = Parser.toHtml(Parser.parseChatGPT(quizService.getRandomDefinition(word, sourceLang)), word);
        model.addAttribute("wordDefinition", wordDefinition);
        }
        catch (Exception e){
            model.addAttribute("wordDefinition", "{start}\nChatGPT error.\nPlease try again.\n{end}"); //(java.net.UnknownHostException: No such host is known (api.openai.com))
        }

        cookieUtils.createCookie(httpServletResponse, "echoLingCurrentLanguage", sourceLang, 3600);
        userService.updateUser(user);

        request.setAttribute("user", user);
        try {
            model.addAttribute("currentLanguage", cookieUtils.getCookie(httpServletRequest, "echoLingCurrentLanguage").get());
        } catch (Exception e) {
            model.addAttribute("currentLanguage", "English");
        }
        return "quiz_page";

    }
}