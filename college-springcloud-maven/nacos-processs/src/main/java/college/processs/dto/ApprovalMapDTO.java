package college.processs.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author K
 * @date 2021/1/28
 */
@Data
public class ApprovalMapDTO {

    private Collection<Long> approvalIdList = new ArrayList<>();
}
