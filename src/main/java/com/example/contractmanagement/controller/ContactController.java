package com.example.contractmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contractmanagement.dto.ContactResponseDTO;
import com.example.contractmanagement.model.Contact;
import com.example.contractmanagement.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody Contact contact) {
        Contact created = contactService.createContact(contact);
        return ResponseEntity.ok(new ContactResponseDTO(created));
    }

    @GetMapping
    public ResponseEntity<Page<ContactResponseDTO>> getAllContacts(Pageable pageable) {
        Page<Contact> contacts = contactService.getAllContacts(pageable);
        Page<ContactResponseDTO> dtoPage = contacts.map(ContactResponseDTO::new);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(contact -> ResponseEntity.ok(new ContactResponseDTO(contact)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> updateContact(@PathVariable Long id, @RequestBody Contact updatedContact) {
        Contact updated = contactService.updateContact(id, updatedContact);
        return ResponseEntity.ok(new ContactResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContactResponseDTO>> searchContacts(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            Pageable pageable) {

        Page<Contact> contacts = contactService.searchByFields(firstName, lastName, email, pageable);
        Page<ContactResponseDTO> dtoPage = contacts.map(ContactResponseDTO::new);

        return ResponseEntity.ok(dtoPage);
    }
}
