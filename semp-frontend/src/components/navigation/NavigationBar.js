import React from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import styled from 'styled-components';

const Styles = styled.div`
  .navbar { background-color: #222; }
  a, .navbar-nav, .navbar-light .nav-link {
    color: #9FFFCB;
    &:hover { color: white; }
  }
  .navbar-brand {
    text-align: center;
    margin: 0;
    position: absolute !important;
    left: 40%;
    right: 40%;
    font-size: 1.4em;
    color: #9FFFCB;
    &:hover { color: white; }
  }
`;

export const NavigationBar = () => (
  <Styles>
    <Navbar expand="lg">
    <Navbar.Brand href="/">Tutorial</Navbar.Brand>
    <Navbar.Toggle aria-controls="basic-navbar-nav"/>
    <Navbar.Collapse id="basic-navbar-nav">
      <Nav className="ml-auto">
      <Nav.Item><Nav.Link href="/">Home</Nav.Link></Nav.Item> 
      <Nav.Item><Nav.Link href="/about">About</Nav.Link></Nav.Item>
      </Nav>
    </Navbar.Collapse>
    </Navbar>
</Styles>
);
