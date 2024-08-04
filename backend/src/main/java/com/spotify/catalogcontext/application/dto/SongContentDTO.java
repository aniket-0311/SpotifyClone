package com.spotify.catalogcontext.application.dto;

import jakarta.persistence.Lob;

import java.util.UUID;

public record SongContentDTO(UUID publicId, @Lob byte[] file, String fileContentType) {
}
