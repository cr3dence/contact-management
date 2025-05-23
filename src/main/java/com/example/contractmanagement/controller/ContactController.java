package com.example.contractmanagement.controller;

import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok(contactService.createContact(contact));
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getAllContacts(Pageable pageable) {
        return ResponseEntity.ok(contactService.getAllContacts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact updatedContact) {
        try {
            return ResponseEntity.ok(contactService.updateContact(id, updatedContact));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {

        if (firstName != null) {
            return ResponseEntity.ok(contactService.searchByFirstName(firstName));
        } else if (lastName != null) {
            return ResponseEntity.ok(contactService.searchByLastName(lastName));
        } else if (email != null) {
            return ResponseEntity.ok(contactService.searchByEmail(email));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
