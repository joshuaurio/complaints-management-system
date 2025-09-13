package com.kcmc.complaints.service;

import com.kcmc.complaints.exception.NotFoundException;
import com.kcmc.complaints.model.*;
import com.kcmc.complaints.repository.*;
import org.springframework.stereotype.Service;

@Service
public class LookupService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final DepartmentRepository deptRepo;
    private final TicketCategoryRepository catRepo;
    private final TicketPriorityRepository prioRepo;
    private final TicketStatusRepository statusRepo;
    private final TicketRepository ticketRepo;

    public LookupService(UserRepository userRepo, RoleRepository roleRepo,
                         DepartmentRepository deptRepo, TicketCategoryRepository catRepo,
                         TicketPriorityRepository prioRepo, TicketStatusRepository statusRepo,
                         TicketRepository ticketRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.deptRepo = deptRepo;
        this.catRepo = catRepo;
        this.prioRepo = prioRepo;
        this.statusRepo = statusRepo;
        this.ticketRepo = ticketRepo;
    }

    public User mustGetUser(Long id){ return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id)); }
    public Role mustGetRole(Short id){ return roleRepo.findById(id).orElseThrow(() -> new NotFoundException("Role not found: " + id)); }
    public Department mustGetDepartment(Integer id){ return deptRepo.findById(id).orElseThrow(() -> new NotFoundException("Department not found: " + id)); }
    public TicketCategory mustGetCategory(Integer id){ return catRepo.findById(id).orElseThrow(() -> new NotFoundException("Category not found: " + id)); }
    public TicketPriority mustGetPriority(Short id){ return prioRepo.findById(id).orElseThrow(() -> new NotFoundException("Priority not found: " + id)); }
    public TicketStatus mustGetStatusByName(String name){
        return statusRepo.findByName(name).orElseThrow(() -> new NotFoundException("Status not found: " + name));
    }
    public Ticket mustGetTicket(Long id){ return ticketRepo.findById(id).orElseThrow(() -> new NotFoundException("Ticket not found: " + id)); }
}
