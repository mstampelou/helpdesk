package gr.aueb.cf.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentWorkloadDTO {
    private String initials;
    private String fullName;
    private long activeCount;
}
