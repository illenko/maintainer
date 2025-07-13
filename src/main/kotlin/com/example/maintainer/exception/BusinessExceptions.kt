package com.example.maintainer.exception

import java.util.UUID

sealed class BusinessException(
    message: String,
) : RuntimeException(message)

class ComponentNotFoundException(
    identifier: String,
) : BusinessException("Component with identifier '$identifier' not found")

class ComponentAlreadyExistsException(
    name: String,
) : BusinessException("Component with name '$name' already exists")

class OutageNotFoundException(
    id: UUID,
) : BusinessException("Outage with id '$id' not found")

class InvalidOutageException(
    message: String,
) : BusinessException(message)

class OutageAlreadyResolvedException(
    id: UUID,
) : BusinessException("Outage with id '$id' is already resolved")
