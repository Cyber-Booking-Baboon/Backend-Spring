package rs.ac.uns.ftn.BookingBaboon.services.users.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;
import java.util.List;

@Service
public class LdapUserService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public void createUser(String userId, String username, String password, String cn, String sn, String role) {
        Name dn = LdapNameBuilder.newInstance()
                .add("ou", "users")
                .add("uid", username)
                .build();

        Attributes attributes = new BasicAttributes();
        BasicAttribute ocattr = new BasicAttribute("objectClass");
        ocattr.add("top");
        ocattr.add("person");
        ocattr.add("organizationalPerson");
        ocattr.add("inetOrgPerson");
        attributes.put(ocattr);
        attributes.put("cn", cn);
        attributes.put("sn", sn);
        attributes.put("uid", username);
        attributes.put("userPassword", password);
        attributes.put("employeeType", role);
        attributes.put("givenName", userId);

        addRole(username,"cn=" + role.toLowerCase() + ",ou=roles");

        ldapTemplate.bind(dn, null, attributes);
    }

    public void addRole(String username, String roleDn) {
        String userDn = "uid=" + username + ",ou=users,dc=example,dc=com";

        ldapTemplate.modifyAttributes(roleDn, new ModificationItem[]{
                new ModificationItem(DirContextOperations.ADD_ATTRIBUTE, new BasicAttribute("member", userDn))
        });
    }
}
