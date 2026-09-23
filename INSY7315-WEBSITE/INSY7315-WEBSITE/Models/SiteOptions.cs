namespace INSY7315_WEBSITE.Models
{
    public class SiteOptions
    {
        public const string SectionName = "Sgula";

        public string PlayStoreUrl { get; set; } = string.Empty;

        public bool PlayStoreAvailable { get; set; }

        public string TherapistName { get; set; } = string.Empty;

        public string ContactEmail { get; set; } = string.Empty;
    }
}
