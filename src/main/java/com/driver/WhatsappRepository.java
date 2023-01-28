package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String,User> moblieUser;
    private HashSet<String> groupUser;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.moblieUser = new HashMap<>();
        this.groupUser = new HashSet<>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile))
            throw new Exception("Number already Exists.");

        userMobile.add(mobile);
        moblieUser.put(mobile,new User(name, mobile));
        return mobile+" Added Successfully with name "+name+" .";
    }

    public Group createGroup(List<User> users){
        if(users.size() == 2) return this.createPersonalChat(users);

        this.customGroupCount++;
        List<User> res = new ArrayList<>();
        for (User user: users) {
            if(!groupUser.contains(user.getMobile())){
                groupUser.add(user.getMobile());
                res.add(user);
            }
        }
        String groupName = "Group " + this.customGroupCount;
        Group group = new Group(groupName, res.size());
        groupUserMap.put(group, res);
        groupMessageMap.put(group,new ArrayList<>());
        adminMap.put(group, res.get(0));
        return group;
    }
    public Group createPersonalChat(List<User> users) {
        String groupName = users.get(1).getName();
        Group personalGroup = new Group(groupName, 2);
        groupUserMap.put(personalGroup, users);
        groupMessageMap.put(personalGroup,new ArrayList<>());
        return personalGroup;
    }
    public int createMessage(String content){
        messageId++;
        Message message = new Message(messageId,content);
        return messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group not present.");
        List<User> list = groupUserMap.get(group);
        boolean flag = false;
        for (User user:list) {
            if(user.getName().equals(sender.getName())){
                flag = true;
                groupMessageMap.get(group).add(message);
                break;
            }
        }
        if(!flag)
            throw new Exception("Sender not present.");
        return groupMessageMap.get(group).size();
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!adminMap.containsKey(group))
            throw new Exception("Group not present.");
        if(!adminMap.get(group).getName().equals(approver.getName()))
            throw new Exception("Approver is not admin.");
        adminMap.put(group,user);
        return "Admin Updated successfully.";
    }
}
