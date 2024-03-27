package prj.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prj.model.Node;
import prj.model.Operation;
import prj.model.Status;
import prj.model.User;
import prj.repositories.NodeRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class NodeService implements IService<Node, Long> {

    private NodeRepository nodeRepository;
    private final TaskScheduler taskScheduler;
    private ErrorMessageService errorMessageService;
    private Random rand = new Random();

    @Autowired
    public NodeService(NodeRepository nodeRepository, TaskScheduler taskScheduler, ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
        this.nodeRepository = nodeRepository;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public Optional<Node> findById(Long id) {
        return nodeRepository.findById(id);
    }

    @Override
    public List<Node> findAll() {
        return nodeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        nodeRepository.deleteById(id);
    }

    public List<Node> findAllByUser(User user) {
        return nodeRepository.findAllByUserId(user.getId());
    }

    @Transactional
    public Optional<Node> findByIdAndUser(Long id, User user) {
        return nodeRepository.findByIdAndUserId(id, user.getId());
    }

    public Node findByNameAndUser(String name, User user) {
        return nodeRepository.findByNameAndUserId(name, user.getId());
    }

    public List<Node> search(User user, String name, List<Status> status, Date dateFrom, Date dateTo) {
        return nodeRepository.searchNodes(name, dateFrom, dateTo, status, user.getId());
    }

    public void deleteNode(Node node) {
        node.setActive(false);
        save(node);
    }


    @Async
    @Transactional
    public void startNode(Long id, User user) {
        Node node = lockRowAndSleep(id, user);
        if (node == null) {
            //
        }
        node.setStatus(Status.RUNNING);
        save(node);
        System.out.println("STARTNODE");

    }

    @Async
    @Transactional
    public void stopNode(Long id, User user) {
        Node node = lockRowAndSleep(id, user);
        if (node == null) {
            //
        }
        node.setStatus(Status.STOPPED);
        save(node);
        System.out.println("STOPPEDNODE");

    }

    @Transactional
    protected Node lockRowAndSleep(Long id, User user) {
        Optional<Node> o = findByIdAndUser(id, user);     // find for update
        Node node = o.get();
        try {
            int t = rand.nextInt(2000);
            t = rand.nextBoolean() ? t + 10000 : 10000 - t;
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return node;
    }

    @Async
    @Transactional
    public void restartNode(Long id, User user) {

        System.out.println("RESTART");

        Optional<Node> o = findByIdAndUser(id, user);     // find for update
        Node node = o.get();

        int t = rand.nextInt(1000);
        t = rand.nextBoolean() ? t + 5000 : 5000 - t;

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node.setStatus(Status.STOPPED);
        save(node);

        System.out.println("RESTART-STOPPED");

        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node.setStatus(Status.RUNNING);
        save(node);
        System.out.println("RESTART-RUNNING");

    }

    @Async
    @Transactional
    public void schedule(Long id, Date scheduleAt, Operation operation, User user) {
        System.out.println("Schedule at: " + scheduleAt + operation + user.getName());
        this.taskScheduler.schedule(() -> {
            try {
                switch (operation) {
                    case START:
                        this.startNode(id, user);
                        System.out.println("Scheduled start at: " + scheduleAt);
                        break;
                    case STOP:
                        this.stopNode(id, user);
                        System.out.println("Scheduled stop at: " + scheduleAt);
                        break;
                    case RESTART:
                        this.restartNode(id, user);
                        System.out.println("Scheduled restart at: " + scheduleAt);
                        break;
                    default:
                        throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessageService.saveErrorMessage(operation, id, user);
            }
        }, scheduleAt);
    }
}
