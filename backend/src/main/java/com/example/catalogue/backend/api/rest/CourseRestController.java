package com.example.catalogue.backend.api.rest;

import com.example.catalogue.backend.service.CourseService;
import com.example.catalogue.backend.util.CourseConverter;
import com.example.catalogue.common.model.Course;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/courses/")
@Tag(name = "Course Catalogue Controller", description = "This REST controller provides services to manage courses in the course catalogue application")
public class CourseRestController {

    private final CourseService courseService;

    @Autowired
    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Provides all courses available in the course catalogue application")
    public Iterable<Course> getAllCourses() {
        var courses = courseService.getAllCourses();
        return StreamSupport.stream(courses.spliterator(), false)
                .map(CourseConverter::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Provides course details for the supplied course id from the course catalogue application")
    public Course getCourseById(@PathVariable("id") Long courseId) {
        var course = courseService.getCourseById(courseId);
        return CourseConverter.toModel(course);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Creates a new course in the course catalogue application")
    public Course createCourse(@Valid @RequestBody Course course) {
        var courseEntity = CourseConverter.toEntity(course);
        var savedCourse = courseService.createCourse(courseEntity);
        return CourseConverter.toModel(savedCourse);
    }

    @PutMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Updates the course details in the course catalogue application for the supplied course id")
    public Course updateCourse(@PathVariable("id") Long courseId, @Valid @RequestBody Course course) {
        var courseEntity = CourseConverter.toEntity(course);
        var updatedCourse = courseService.updateCourse(courseId, courseEntity);
        return CourseConverter.toModel(updatedCourse);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes the course details for the supplied course id from the course catalogue application")
    @RolesAllowed("ADMIN")
    public void deleteCourseById(@PathVariable("id") Long courseId) {
        courseService.deleteCourseById(courseId);
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes all courses from the course catalogue application")
    @RolesAllowed("ADMIN")
    public void deleteCourses() {
        courseService.deleteCourses();
    }

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Search similar courses based on provided parameters from the course catalogue application")
    public Iterable<Course> searchCourses(@RequestParam(required = false, defaultValue = "") String name,
                                          @RequestParam(required = false, defaultValue = "") String category,
                                          @RequestParam(required = false, defaultValue = "0") Integer rating) {
        var result = courseService.searchSimilarCourses(name, category, rating);
        return StreamSupport.stream(result.spliterator(), false)
                .map(CourseConverter::toModel)
                .collect(Collectors.toList());
    }

}
