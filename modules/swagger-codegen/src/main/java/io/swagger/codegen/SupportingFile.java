package io.swagger.codegen;

public class SupportingFile {
    public String templateFile;
    public String folder;
    public String destinationFilename;

    public SupportingFile(String templateFile, String folder, String destinationFilename) {
        this.templateFile = templateFile;
        this.folder = folder;
        this.destinationFilename = destinationFilename;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((destinationFilename == null) ? 0 : destinationFilename
						.hashCode());
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
		result = prime * result
				+ ((templateFile == null) ? 0 : templateFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupportingFile other = (SupportingFile) obj;
		if (destinationFilename == null) {
			if (other.destinationFilename != null)
				return false;
		} else if (!destinationFilename.equals(other.destinationFilename))
			return false;
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		if (templateFile == null) {
			if (other.templateFile != null)
				return false;
		} else if (!templateFile.equals(other.templateFile))
			return false;
		return true;
	}
}