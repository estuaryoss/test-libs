package com.github.estuaryoss.libs.zephyruploader.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ZephyrMetaInfo {
    int folderId;
    String cycleId;
    String projectId;
    String versionId;
}
