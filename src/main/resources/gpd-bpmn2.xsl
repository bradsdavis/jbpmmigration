<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:jpdl="urn:jbpm.org:jpdl-3.2"
  xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">

  <xsl:output method="xml" indent="yes" />

  <xsl:variable name="allowedSymbols" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'" />
  
  <xsl:template match="/">
    <xsl:apply-templates select="root-container" />
  </xsl:template>

  <xsl:template match="root-container">
  	<bpmndi:BPMNDiagram xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
      xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
      xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:drools="http://www.jboss.org/drools">
      <bpmndi:BPMNPlane>
      	<xsl:apply-templates select="//node" />
      </bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
  </xsl:template>

  <xsl:template match="node">
	<bpmndi:BPMNShape xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC">
	  <xsl:attribute name="bpmnElement">
		<xsl:value-of select="@name" />
      </xsl:attribute>
      <dc:Bounds>
      	<xsl:attribute name="x">
			<xsl:value-of select="@x" />
      	</xsl:attribute>
      	<xsl:attribute name="y">
			<xsl:value-of select="@y" />
      	</xsl:attribute>
      	<xsl:attribute name="width">
			<xsl:value-of select="@width" />
      	</xsl:attribute>
      	<xsl:attribute name="height">
			<xsl:value-of select="@height" />
      	</xsl:attribute>
      </dc:Bounds>
    </bpmndi:BPMNShape>
  </xsl:template>

  <!-- Strip the white space from the result. -->
  <xsl:template match="text()">
    <xsl:value-of select="normalize-space()" />
  </xsl:template>

</xsl:stylesheet>

