import React, { useState } from 'react';
import { Link } from 'react-router-dom'; // Assuming you're using React Router for navigation

const Sidebar = () => {
  // State for collapsing the menu items
  const [collapseTwo, setCollapseTwo] = useState(false);
  const [collapseUtilities, setCollapseUtilities] = useState(false);
  const [collapsePages, setCollapsePages] = useState(false);

  const toggleCollapse = (setter) => {
    setter((prevState) => !prevState);
  };

  return (
    <ul className="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
      {/* Sidebar - Brand */}
      <Link className="sidebar-brand d-flex align-items-center justify-content-center" to="/index">
        <div className="sidebar-brand-icon rotate-n-15">
          <i className="fas fa-laugh-wink"></i>
        </div>
        <div className="sidebar-brand-text mx-3">SB Admin <sup>2</sup></div>
      </Link>

      {/* Divider */}
      <hr className="sidebar-divider my-0" />

      {/* Nav Item - Dashboard */}
      <li className="nav-item active">
        <Link className="nav-link" to="/index">
          <i className="fas fa-fw fa-tachometer-alt"></i>
          <span>Dashboard</span>
        </Link>
      </li>

      {/* Divider */}
      <hr className="sidebar-divider" />

      {/* Heading */}
      <div className="sidebar-heading">
        Interface
      </div>

      {/* Nav Item - Components Collapse Menu */}
      <li className="nav-item">
        <a
          className="nav-link collapsed"
          href="#!"
          onClick={() => toggleCollapse(setCollapseTwo)}
          aria-expanded={collapseTwo ? "true" : "false"}
          aria-controls="collapseTwo"
        >
          <i className="fas fa-fw fa-cog"></i>
          <span>Components</span>
        </a>
        <div id="collapseTwo" className={`collapse ${collapseTwo ? 'show' : ''}`} aria-labelledby="headingTwo">
          <div className="bg-white py-2 collapse-inner rounded">
            <h6 className="collapse-header">Custom Components:</h6>
            <button className="collapse-item">Buttons</button> {/* Chỉ cần button, không cần Link */}
            <button className="collapse-item">Cards</button>   {/* Chỉ cần button, không cần Link */}
          </div>
        </div>
      </li>

      {/* Nav Item - Utilities Collapse Menu */}
      <li className="nav-item">
        <a
          className="nav-link collapsed"
          href="#!"
          onClick={() => toggleCollapse(setCollapseUtilities)}
          aria-expanded={collapseUtilities ? "true" : "false"}
          aria-controls="collapseUtilities"
        >
          <i className="fas fa-fw fa-wrench"></i>
          <span>Utilities</span>
        </a>
        <div id="collapseUtilities" className={`collapse ${collapseUtilities ? 'show' : ''}`} aria-labelledby="headingUtilities" data-parent="#accordionSidebar">
          <div className="bg-white py-2 collapse-inner rounded">
            <h6 className="collapse-header">Custom Utilities:</h6>
            <Link className="collapse-item" to="/utilities-color">Colors</Link>
            <Link className="collapse-item" to="/utilities-border">Borders</Link>
            <Link className="collapse-item" to="/utilities-animation">Animations</Link>
            <Link className="collapse-item" to="/utilities-other">Other</Link>
          </div>
        </div>
      </li>

      {/* Divider */}
      <hr className="sidebar-divider" />

      {/* Heading */}
      <div className="sidebar-heading">
        Addons
      </div>

      {/* Nav Item - Pages Collapse Menu */}
      <li className="nav-item">
        <a
          className="nav-link collapsed"
          href="#!"
          onClick={() => toggleCollapse(setCollapsePages)}
          aria-expanded={collapsePages ? "true" : "false"}
          aria-controls="collapsePages"
        >
          <i className="fas fa-fw fa-folder"></i>
          <span>Pages</span>
        </a>
        <div id="collapsePages" className={`collapse ${collapsePages ? 'show' : ''}`} aria-labelledby="headingPages" data-parent="#accordionSidebar">
          <div className="bg-white py-2 collapse-inner rounded">
            <h6 className="collapse-header">Login Screens:</h6>
            <Link className="collapse-item" to="/login">Login</Link>
            <Link className="collapse-item" to="/register">Register</Link>
            <Link className="collapse-item" to="/forgot-password">Forgot Password</Link>
            <div className="collapse-divider"></div>
            <h6 className="collapse-header">Other Pages:</h6>
            <Link className="collapse-item" to="/404">404 Page</Link>
            <Link className="collapse-item" to="/blank">Blank Page</Link>
          </div>
        </div>
      </li>

      {/* Nav Item - Charts */}
      <li className="nav-item">
        <Link className="nav-link" to="/charts">
          <i className="fas fa-fw fa-chart-area"></i>
          <span>Charts</span>
        </Link>
      </li>

      {/* Nav Item - Tables */}
      <li className="nav-item">
        <Link className="nav-link" to="/tables">
          <i className="fas fa-fw fa-table"></i>
          <span>Tables</span>
        </Link>
      </li>

      {/* Divider */}
      <hr className="sidebar-divider d-none d-md-block" />

      {/* Sidebar Toggler (Sidebar) */}
      <div className="text-center d-none d-md-inline">
        <button className="rounded-circle border-0" id="sidebarToggle"></button>
      </div>

      {/* Sidebar Message */}
      <div className="sidebar-card d-none d-lg-flex">
        <img className="sidebar-card-illustration mb-2" src="img/undraw_rocket.svg" alt="..." />
        <p className="text-center mb-2"><strong>SB Admin Pro</strong> is packed with premium features, components, and more!</p>
        <a className="btn btn-success btn-sm" href="https://startbootstrap.com/theme/sb-admin-pro">Upgrade to Pro!</a>
      </div>
    </ul>
  );
};

export default Sidebar;
