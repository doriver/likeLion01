package com.lion.demo.chatting;

import com.lion.demo.entity.User;
import com.lion.demo.service.UserService;
import com.lion.demo.util.TimeUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/chatting")
public class ChattingController {

    @Autowired private ChatMessageService chatMessageService;
    @Autowired private RecipientService recipientService;
    @Autowired private UserService userService;
    @Autowired private TimeUtil timeUtil;

    @Value("${server.port}") private String serverPort;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        session.setAttribute("chattingStatus", "home");
        session.setAttribute("menu","chatting");
        session.setAttribute("serverPort",serverPort);

        String sessUid = (String) session.getAttribute("sessUid");
        User user = userService.findByUid(sessUid);
        model.addAttribute("user",user);

        return "chatting/home";
    }

    @GetMapping("/getChatterList")
    @ResponseBody
    public ResponseEntity<List<Chatter>> getChatterList(@RequestParam String userId) {
        List<Recipient> friendList = recipientService.getFriendList(userId);
//        HashSet<Recipient> friendSet = new HashSet<>(friendList); // 채팅홈 중복문제 해결위해 임시로했는데 안됨
        
        List<Chatter> chatterList = new ArrayList<>();
        for (Recipient recipient : friendList) {
            User friend = recipient.getUser().getUid().equals(userId) ? recipient.getFriend() : recipient.getUser();
            ChatMessage chatMessage = chatMessageService.getLastChatMessage(userId, friend.getUid());
            int newCount = chatMessageService.getNewCount(friend.getUid(), userId);
            Chatter chatter = Chatter.builder().
                    friendUid(friend.getUid()).friendUname(friend.getUname()).friendProfileUrl(friend.getProfileUrl())
                    .message(chatMessage.getMessage())
                    .timeStr(timeUtil.timeAgo(chatMessage.getTimestamp()))
                    .newCount(newCount)
                    .build();
            chatterList.add(chatter);
        }
        return ResponseEntity.ok(chatterList);
    }


    @GetMapping("/mock")
    public String mockForm() {
        return "chatting/mock";
    }

    @PostMapping("/mock")
    public String mockProc(String senderUid, String recipientUid, String message, LocalDateTime timestamp) {
        User sender = userService.findByUid(senderUid);
        User recipient = userService.findByUid(recipientUid);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender).recipient(recipient).message(message).timestamp(timestamp).hasRead(0)
                .build();

        chatMessageService.insertChatMessage(chatMessage);
//        recipientService.insertFriend(sender, recipient);
        return "redirect:/chatting/mock";
    }
}
