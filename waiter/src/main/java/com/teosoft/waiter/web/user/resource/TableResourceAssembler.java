package com.teosoft.waiter.web.user.resource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.teosoft.waiter.jpa.domain.user.Table;
import com.teosoft.waiter.jpa.domain.user.TableRepository;

@Component
public class TableResourceAssembler {

	@Autowired
	private TableRepository tableRepository;

	public TableResource toResource(Table entity, boolean light) {
		TableResource resource = new TableResource();
		resource.setId(entity.getId());
		resource.setPosition(entity.getPosition());
		resource.setIdentifier(entity.getIdentifier());
		resource.setCreationDate(entity.getCreationDate().toDate());
		resource.setLastModifiedDate(entity.getLastModifiedDate().toDate());
		if (!light) {
			resource.setQrCode((StringUtils.isNotBlank(entity.getCode())) ? generateQrCodeAsBase64(entity.getCode()) : null);
			resource.setDescription(entity.getDescription());
		}
		return resource;
	}

	public List<TableResource> toResources(Iterable<Table> entities, boolean light) {
		List<TableResource> resources = new ArrayList<>();
		for (Table entity : entities) {
			resources.add(toResource(entity, light));
		}
		return resources;
	}

	private Table toEntity(TableResource resource) {
		Table entity = (resource.getId() == null) ? new Table() : tableRepository.findOne(resource.getId());
		entity.setIdentifier(resource.getIdentifier());
		entity.setDescription(resource.getDescription());
		return entity;
	}

	public Set<Table> toEntities(Iterable<TableResource> resources) {
		Set<Table> entities = new LinkedHashSet<>();
		int position = 0;
		for (TableResource resource : resources) {
			Table entity = toEntity(resource);
			entity.setPosition(position++);
			entities.add(entity);
		}
		return entities;
	}

	private String generateQrCodeAsBase64(String text) {
		try {
			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200), "PNG", pngOutputStream);
			byte[] pngData = pngOutputStream.toByteArray();

			return Base64.encodeBase64String(pngData);
		} catch (Exception e) {
			return null;
		}
	}

}
