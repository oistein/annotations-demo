package no.norgesgruppen.demo;

import no.norgesgruppen.demo.annotations.Getter;
import no.norgesgruppen.demo.annotations.Setter;

public abstract class KundeSpec {

	@Getter
	protected String navn;

	@Getter @Setter
	protected Long grossistLagerNummer;

	@Getter @Setter
	protected Long gln;

	@Getter @Setter
	protected Long kundeKategoriNummer;

}
