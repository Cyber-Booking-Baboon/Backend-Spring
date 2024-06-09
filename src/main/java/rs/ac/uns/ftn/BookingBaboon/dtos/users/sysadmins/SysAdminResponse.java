package rs.ac.uns.ftn.BookingBaboon.dtos.users.sysadmins;

import lombok.Data;

@Data
public class SysAdminResponse {

    private Long id;

    private String email;

    private String jwt;
}
