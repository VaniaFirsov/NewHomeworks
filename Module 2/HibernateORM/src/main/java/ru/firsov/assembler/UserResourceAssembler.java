package ru.firsov.assembler;

import ru.firsov.controller.UserController;
import ru.firsov.dto.UserResponseDTO;
import ru.firsov.dto.UserResponseResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserResourceAssembler implements
        RepresentationModelAssembler<UserResponseDTO, UserResponseResource> {

    @Override
    public UserResponseResource toModel(UserResponseDTO user) {
        UserResponseResource resource = new UserResponseResource(user);

        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(user.getId())).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .updateUser(user.getId(), null)).withRel("update"));
        resource.add(linkTo(methodOn(UserController.class)
                .deleteUser(user.getId())).withRel("delete"));

        resource.add(linkTo(UserController.class)
                .slash(user.getId()).withRel("user"));
        resource.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withRel("all-users"));

        return resource;
    }

    @Override
    public CollectionModel<UserResponseResource> toCollectionModel(
            Iterable<? extends UserResponseDTO> entities) {

        List<UserResponseResource> resources = StreamSupport
                .stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<UserResponseResource> collectionModel =
                CollectionModel.of(resources);

        collectionModel.add(linkTo(methodOn(UserController.class)
                .getAllUsers()).withSelfRel());
        collectionModel.add(linkTo(methodOn(UserController.class)
                .createUser(null)).withRel("create-user"));

        return collectionModel;
    }
}