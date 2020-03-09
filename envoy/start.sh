#!/usr/bin/env sh

protoc --proto_path="${GOOGLEAPIS_DIR}" --proto_path=src/main/proto/ --include_imports --include_source_info --descriptor_set_out=proto.pb src/main/proto/messages.proto
envoy -c envoy/config.yaml
