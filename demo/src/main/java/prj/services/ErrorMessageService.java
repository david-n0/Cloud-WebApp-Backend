package prj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prj.model.ErrorMessage;
import prj.model.Operation;
import prj.model.User;
import prj.repositories.ErrorMessageRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ErrorMessageService {

    private ErrorMessageRepository errorMessageRepository;

    @Autowired
    public ErrorMessageService(ErrorMessageRepository errorMessageRepository) {
        this.errorMessageRepository = errorMessageRepository;
    }

    public void saveErrorMessage(Operation operation, Long nodeId, User user) {
        System.out.println("TU SAM " +operation + nodeId + user.getName());
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setNodeId(nodeId);
        errorMessage.setUser(user);
        errorMessage.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        switch (operation) {
            case START:
                errorMessage.setStatus(Operation.START);
                errorMessage.setMessage("Failed to start node");
                errorMessageRepository.save(errorMessage);
                break;
            case STOP:
                errorMessage.setStatus(Operation.STOP);
                errorMessage.setMessage("Failed to stop machine");
                errorMessageRepository.save(errorMessage);
                break;
            case RESTART:
                errorMessage.setStatus(Operation.RESTART);
                errorMessage.setMessage("Failed to restart machine");
                errorMessageRepository.save(errorMessage);
                break;
            default:

        }
    }

    public List<ErrorMessage> findAllByUser(User user){
        return errorMessageRepository.findAllByUserId(user.getId());
    }
}
