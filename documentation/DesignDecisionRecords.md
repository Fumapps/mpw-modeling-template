# Design Decisions

## Configuration

### Only support PNG

* make it simple, so no further "type" flag has to be carried
* in UI, simply filter only for PNG files

### Images are encoded as Base64

* allow to store the image contents in the configuration
* be independent on the source file system