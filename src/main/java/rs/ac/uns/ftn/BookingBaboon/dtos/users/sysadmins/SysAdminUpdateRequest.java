package rs.ac.uns.ftn.BookingBaboon.dtos.users.sysadmins;

import lombok.Data;

@Data
public class SysAdminUpdateRequest {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String address;

    private String phoneNumber;

    private String jwt;
}