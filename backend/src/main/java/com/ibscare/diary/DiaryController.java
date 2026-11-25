package com.ibscare.diary;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo/entries")
public class DiaryController {
    private final DiaryService service;

    public DiaryController(DiaryService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiaryDto> list() { return service.list(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiaryDto create(@Valid @RequestBody DiaryDto request) { return service.create(request); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }
}
