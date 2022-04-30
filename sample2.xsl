<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" omit-xml-declaration="yes" />

    <xsl:strip-space elements="*" />
  
  <!-- This is to remove empty extra lines -->>
  
    <xsl:template match="@*|node()">
    <xsl:if test=". != '' or ./@* != ''">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:if>
    </xsl:template>
</xsl:stylesheet>