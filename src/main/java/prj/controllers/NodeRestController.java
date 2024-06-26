package prj.controllers;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import prj.model.*;
import prj.services.NodeService;
import prj.services.UserService;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/nodes")
public class NodeRestController {

    private final NodeService nodeService;
    private final UserService userService;

    public NodeRestController(NodeService nodeService, UserService userService) {
        this.nodeService = nodeService;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchNodes(@RequestParam("name") String name, @RequestParam("status") List<String> status, @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<Date> dateFrom, @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<Date> dateTo){
        if (!authCheck()) {
            return ResponseEntity.status(403).build();
        }

        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_search_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Status> statusList = new ArrayList<>();
        if (!status.isEmpty()) {
            for (String s : status) {
                if (s.equals("STOPPED")) {
                    statusList.add(Status.STOPPED);
                }
                if (s.equals("RUNNING")) {
                    statusList.add(Status.RUNNING);
                }
            }
        } else {
            statusList.add(Status.STOPPED);
            statusList.add(Status.RUNNING);
        }

        if (name.equals(""))
            name = null;

        Date dateFromVal = null;
        Date dateToVal = null;

        if (dateFrom.isPresent()) {
            dateFromVal = dateFrom.get();
        }

        if (dateTo.isPresent()) {
            dateToVal = dateTo.get();
        }

        List<Node> nodes = nodeService.search(user, name, statusList, dateFromVal, dateToVal);

        System.out.println(nodes);
        System.out.println(name);
        System.out.println(statusList);
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    };

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> all(){
        if (!authCheck()) {
            return ResponseEntity.status(403).build();
        }

        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_search_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);
        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Node> nodes = nodeService.findAllByUser(user);

        return new ResponseEntity<>(nodes, HttpStatus.OK);
    };

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createNode(@RequestBody NodeNameRequest request){

        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_create_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        if(user == null)
            return ResponseEntity.badRequest().build();

        if (nodeService.findByNameAndUser(request.getName(), user) != null)
            return ResponseEntity.badRequest().build();

        Node node = new Node();
        node.setName(request.getName());
        node.setActive(true);
        node.setUser(user);
        node.setStatus(Status.STOPPED);
        node.setCreatedAt(new Date(Calendar.getInstance().getTime().getTime()));

//        user.getNodes().add(node);
//        userService.save(user); TODO

        return ResponseEntity.ok(nodeService.save(node));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> destroyNode(@PathVariable Long id) {
        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_destroy_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        Optional<Node> optionalNode = nodeService.findByIdAndUser(id, user);

        if(!optionalNode.isPresent())
            return ResponseEntity.notFound().build();

        Node node = optionalNode.get();

        if (node.getStatus() == Status.RUNNING)
            return ResponseEntity.status(403).build();

        nodeService.deleteNode(node);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startNode(@RequestParam("id") Long id){
        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_start_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Node> optionalNode = nodeService.findByIdAndUser(id, user);
        if(!optionalNode.isPresent())
            return ResponseEntity.notFound().build();

        Node node = optionalNode.get();

        if (node.getStatus() != Status.STOPPED)
            return ResponseEntity.badRequest().build();

        nodeService.startNode(id, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopNode(@RequestParam("id") Long id){
        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_stop_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Node> optionalNode = nodeService.findByIdAndUser(id, user);
        if(!optionalNode.isPresent())
            return ResponseEntity.notFound().build();

        Node node = optionalNode.get();
        if (node.getStatus() != Status.RUNNING)
            return ResponseEntity.badRequest().build();

        nodeService.stopNode(id, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/restart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restartNode(@RequestParam("id") Long id){
        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_restart_nodes)) {
            return ResponseEntity.status(403).build();
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Node> optionalNode = nodeService.findByIdAndUser(id, user);
        if(!optionalNode.isPresent())
            return ResponseEntity.notFound().build();

        Node node = optionalNode.get();
        if (node.getStatus() != Status.RUNNING)
            return ResponseEntity.badRequest().build();

        nodeService.restartNode(id, user);

        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleOperation(@RequestParam("id") Long id, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date scheduleAt, @RequestParam("operation") String operationStr) {
        if (!authCheck()) {
            return ResponseEntity.internalServerError().body(null);
        }

        Operation operation = null;
        if (operationStr.equals("STOP")) {
            if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_stop_nodes))
                return ResponseEntity.status(403).build();
            operation = Operation.STOP;
        }
        else if (operationStr.equals("START")) {
            if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_start_nodes))
                return ResponseEntity.status(403).build();
            operation = Operation.START;
        }
        else if (operationStr.equals("RESTART")) {
            if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Permissions.can_restart_nodes))
                return ResponseEntity.status(403).build();
            operation = Operation.RESTART;
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(username);

        if(user == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Node> optionalNode = nodeService.findByIdAndUser(id, user);

        if (!optionalNode.isPresent())
            return ResponseEntity.status(204).build();

        nodeService.schedule(id, scheduleAt, operation, user);
        return ResponseEntity.ok().build();
    }

    private boolean authCheck(){
        if (SecurityContextHolder.getContext() == null ||
                SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getAuthorities() == null)
            return false;
        return true;
    }
}
